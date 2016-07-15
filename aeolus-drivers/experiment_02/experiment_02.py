"""
Experiment #02: Adding parameter 'c' and averaging across multiple partitions.

Experiment no. 2 runs the learning tasks seen in experiment no. 1 but with
some important modifications:
    (1) The 'c' (time scale) parameter is allowed to vary (this allows
        us to learn the proper spatiotemportal anistropy paramter).
    (2) Each conf is run across 20 partitions, and the error stats are
        averaged (this reduces the effect that a unique partition has on
        our error statistic results).
Like the script from the previous experiment, this script produces a
"results.csv" file in HDFS as output.
"""

import csv
import StringIO

import kfold
import point

# from pyspark import SparkConf, SparkContext
#
#
# CONF = SparkConf().setAppName('Experiment #02')
# SC = SparkContext(conf=CONF)


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
            ',' + str(result_record[2]) +               # MARE
            ',' + str(result_record[3]) + '\n')         # RMSPE


def main():
    """ Application main. """

    # build the list of KFoldConf objects under analysis
    # 3 partitions
    # 10 folds
    # N = [3, 4, 5, 6, 7, 8]
    # P = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5]
    # C = {0.001: 0.001: 0.025} U {0.025: 0.025: 2}

    # STEPS:
    #   (1) Set i <- 0
    #   (2) Load partition i csv file
    #   (3) Run all confs in KFOLDCONFLIST in parallel on partition i
    #   (4) append results to RESULTLIST
    #   (5) i <- i + 1 and go to (2)
    #   Finally: Write RESULTLIST as CSV to HDFS
    # TODO: add conf_id to KFoldConf objects in list
    pass

if __name__ == "__main__":
    main()
