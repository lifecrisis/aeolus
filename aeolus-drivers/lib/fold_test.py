from pyspark import SparkConf, SparkContext

import kfold
import point

CONF = SparkConf().setMaster('local').setAppName('Fold Test')
SC = SparkContext(conf=CONF)


def main():
    """ Application main. """
    kfold_conf = kfold.KFoldConf(10, 3, 5, None, 0.1086)

    file_rdd = SC.textFile('../../data/pm25_2009_measured.csv')
    point_list = point.load_pm25_points(file_rdd).collect()
    point_list_brd = SC.broadcast(point_list)

    mare_result = kfold.mare(kfold_conf, point_list_brd)
    rmspe_result = kfold.rmspe(kfold_conf, point_list_brd)

    print "KFold MARE: " + str(mare_result)
    print "KFold RMSPE: " + str(rmspe_result)

if __name__ == "__main__":
    main()
