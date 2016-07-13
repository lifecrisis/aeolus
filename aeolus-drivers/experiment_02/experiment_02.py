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

from pyspark import SparkConf, SparkContext

CONF = SparkConf().setAppName('Experiment #02')
SC = SparkContext(conf=CONF)


def load_partition(partition_id):
    # load partitions from HDFS, allows running in cluster mode...
    pass


def report(result_record):
    # write results as CSV to HDFS, allows running in cluster mode...
    pass


def main():
    """ Application main. """
    # STEPS:
    #   (0) Build KFOLDCONFLIST and create empty RESULTLIST
    #   (1) Set i <- 0
    #   (2) Load partition i csv file
    #   (3) Run all confs in KFOLDCONFLIST in parallel on partition i
    #   (4) append results to RESULTLIST
    #   (5) i <- i + 1 and go to (2)
    #   Finally: Write RESULTLIST as CSV to HDFS
    # TODO: Possibly add a conf ID in advance to group by them later
    pass

if __name__ == "__main__":
    main()
