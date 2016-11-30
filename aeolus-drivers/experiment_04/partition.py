"""
Generate distinct partitions over which to average k-fold results.

This module generates 20 distinct partitions of our dataset. By "partition",
we mean a shuffled, headerless CSV files derived from pm25_2009_measured.csv).
These partitions are then used to average out the effect of particular
partitions on our error statistics.
"""

import random


def read_records():
    """ Return a list of the CSV records from "pm25_2009_measured.csv". """
    f = open('../../data/pm25_2009_measured.csv', 'r')
    record_list = f.readlines()
    f.close()
    return record_list[1:]
RECORD_LIST = read_records()


def write_records(filename):
    """ Shuffle the global record list and write to "filename". """
    global RECORD_LIST
    random.shuffle(RECORD_LIST)
    f = open(filename, 'w')
    f.writelines(RECORD_LIST)
    f.close()


def main():
    """ Application main. """
    filenames = ['partitions/partition_%02d.csv' % i for i in xrange(3)]
    for name in filenames:
        write_records(name)

if __name__ == "__main__":
    main()
