"""
Experiment #1: Re-implementation of experiment in 2014 Li, et al. paper.

TODO: Enter module docstring here...
"""

import random

from pyspark import SparkConf, SparkContext

import kfold
import point

CONF = SparkConf().setAppName('Experiment #01')
SC = SparkContext(conf=CONF)


def report(record):
    """
    Return a report string given a record from a result RDD.

    Here, record is a three-tuple of the form (KFoldConf, float, float). The
    last two floats are the MARE and RMSPE results.
    """
    result = ('Neighbors : ' + str(record[0].neighbors) +
              '\nPower     : ' + str(record[0].power) +
              '\nMARE      : ' + str(record[1]) +
              '\nRMSPE     : ' + str(record[2]) + '\n')
    return result


def main():
    """ Application main. """

    # establish bases for configurations (neighbors and interpolation exponent)
    N = [3, 4, 5, 6, 7]
    P = [1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5]

    # build and parallelize list of KFoldConf objects
    conf_list = [kfold.KFoldConf(10, n, p, None, 0.1086) for n in N for p in P]

    # Don't forget to slice the data into partitions accordingly.
    conf_rdd = SC.parallelize(conf_list, 44)
    conf_rdd.cache()

    # load PMPoint objects from storage, shuffle, and broadcast
    point_list = point.load_pm25_file('../../data/pm25_2009_measured.csv')
    random.shuffle(point_list)

    # TODO: Remove before submitting to yarn (temporarily smaller dataset)
    # point_list = point_list[0: 500]
    point_list_brd = SC.broadcast(point_list)

    result_rdd = conf_rdd.map(lambda conf: (conf,
                                            kfold.mare(conf, point_list_brd),
                                            kfold.rmspe(conf, point_list_brd)))
    report_rdd = result_rdd.map(report)

    # This is the first action, so it will be the only "stage" in our job.
    for r in report_rdd.collect():
        print r

if __name__ == "__main__":
    main()
