"""
Experiment #1: Re-implementation of experiment in 2014 Li, et al. paper.

TODO: Enter module docstring here...
"""

# standard library import(s)
import random

# third-party import(s)
import pyspark

# local/application-specific import(s)
import kfold
import point

CONF = pyspark.SparkConf().setAppName('Experiment #01')
SC = pyspark.SparkContext(conf=CONF)


def report(record):
    """
    Return a report string (in CSV format) given a record from a result RDD.

    Here, "record" is a three-tuple of the form (KFoldConf, float, float). The
    last two floats are the MARE and RMSPE results, respectively.
    """
    return (str(record[0].neighbors) +      # neighbors
            ',' + str(record[0].power) +    # power
            ',' + str(record[1]) +          # MARE
            ',' + str(record[2]) + '\n')    # RMSPE


def main():
    """ Application main. """

    N = [3, 4, 5, 6, 7]
    P = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5]

    conf_list = [kfold.KFoldConf(10, n, p, None, 0.1086) for n in N for p in P]
    conf_rdd = SC.parallelize(conf_list, 45).cache()

    point_list = point.load_pm25_file('../../data/pm25_2009_measured.csv')
    random.shuffle(point_list)
    # The following was used to test execution of this script locally.
    # point_list = point_list[:250]
    point_list_brd = SC.broadcast(point_list)

    def fold(conf):
        return (conf,
                kfold.mare(conf, point_list_brd),
                kfold.rmspe(conf, point_list_brd))
    report_rdd = conf_rdd.map(fold).map(report)
    report_rdd.saveAsTextFile(
        "hdfs:///user/jf00936/aeolus/experiment_01/results")

    # The following was used to test execution of this script locally.
    # with open('results.txt', 'w') as results_file:
    #     results_file.write("neighbors,power,mare,rmspe\n")
    #     results_file.writelines(report_rdd.collect())

if __name__ == "__main__":
    main()
