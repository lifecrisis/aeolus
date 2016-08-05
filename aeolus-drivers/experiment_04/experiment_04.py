"""
Experiment #04: Using bagging to reduce variance and overfitting.

Our experiment is the same as in experiment #2, really. However, we use
the "bagging" technique in our mare and rmspe methods, as specified by
professor Tong. The relevant parameter selections that are explored are:
    (1) m = 3
    (2) alpha = {0.50, 0.75}

Expect a "results.csv" as output.
"""

import csv
import StringIO

import kfold
import point

from pyspark import SparkConf, SparkContext


CONF = SparkConf()
SC = SparkContext(conf=CONF)


def load_partition(partition_id):
    """
    Return a list of PMPoints in the order of the given partition.

    The kfold operations (mare, rmspe) are responsible for actually breaking
    up a partition into a list of lists. The order of the loaded file
    is what determins the "random" nature of this list of lists. Here, we
    simply want the PMPoint objects in the proper order.
    """
    partition_filename = "partitions/partition_%02d.csv" % partition_id
    f = open(partition_filename, 'r')

    def load_record(record):
        """ Parse a single CSV record. """
        result = StringIO.StringIO(record)
        fieldnames = ['site_id',
                      'year',
                      'month',
                      'day',
                      'longitude',
                      'latitude',
                      'pm25']
        reader = csv.DictReader(result, fieldnames)
        return reader.next()

    result = map(lambda rec: point.PMPoint(**rec), map(load_record, f))
    f.close()

    return result


def report(result_record):
    """
    Return a report string (in CSV format) given a record from a result RDD.

    This report method is unique to this experiment. This makes sense because
    output differs across all experiments. Here, the "result_record" takes
    the form:

        (partition_id, KFoldConf, float, float)

    The first element is an int. The last two are floating point numbers
    storing the MARE and RMSPE results respectively.

    This method also accesses a special "id" attribute added to KFoldConf
    objects for grouping them later.
    """
    return (str(result_record[0]) +                     # partition_id
            ',' + str(result_record[1].conf_id) +       # conf_id
            ',' + str(result_record[1].folds) +         # folds
            ',' + str(result_record[1].neighbors) +     # neighbors
            ',' + str(result_record[1].power) +         # power
            ',' + str(result_record[1].time_scale) +    # time_scale
            ',' + str(result_record[1].alpha) +         # alpha
            ',' + str(result_record[1].m) +             # m
            ',' + str(result_record[2]) +               # MARE
            ',' + str(result_record[3]))                # RMSPE


def main():
    """ Application main. """

    K = [10]                                        # folds
    N = [3, 4, 5, 6, 7, 8]                          # neighbors
    P = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5]         # powers
    C = [0.001 * i for i in range(1, 25)]           # time_scales
    C.extend([0.025 * i for i in range(1, 81)])
    A = [0.50, 0.75]                                # alphas
    M = [3]                                         # bags

    # build the list and then RDD of KFoldConf objects under analysis
    conf_list = [kfold.KFoldConf(k, n, p, None, c, a, m)
                 for k in K
                 for n in N
                 for p in P
                 for c in C
                 for a in A
                 for m in M]

    # add incremental "conf_id" attribute to each KFoldConf object
    for i, conf in enumerate(conf_list):
        conf.conf_id = i
    conf_rdd = SC.parallelize(conf_list, 150).cache()

    # run learning tasks for each partition
    for i in range(3):
        point_list = load_partition(i)
        point_list_brd = SC.broadcast(point_list)

        def fold(conf):
            """ Return a result tuple for the given configuration. """
            return (i,                                  # partition_id
                    conf,                               # KFoldConf object
                    kfold.mare(conf, point_list_brd),   # MARE statistic
                    kfold.rmspe(conf, point_list_brd))  # RMSPE statistic

        report_rdd = conf_rdd.map(fold).map(report)
        report_rdd.saveAsTextFile('results/partition%02d' % i)

    # collect all results into one rdd, then into one file
    result_rdds = [SC.textFile('results/partition0%d/' % i) for i in range(3)]
    results = result_rdds[0].\
        union(result_rdds[1]).\
        union(result_rdds[2]).\
        collect()
    with open('results.csv', 'w') as output:
        results = map(lambda line: line + '\n', results)
        output.writelines(results)

if __name__ == "__main__":
    main()
