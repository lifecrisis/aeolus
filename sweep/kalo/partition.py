#! /usr/bin/env python

# "partition.py": A script that gives two random partitions of the records in 
# the file "data/pm25_2009_measured.csv". This serves two purposes: 
#     (1) It allows Marc Kalo and I to use the same partition to compare our 
#         estimation procedures, resolving a format discrepancy. 
#     (2) It replaces the invalid partition that he was using in his own 
#         tests.

from os import path

import os
import random


BASE_DIR = path.dirname(path.dirname(path.dirname(path.abspath(__file__))))
DATA_DIR = path.join(BASE_DIR, 'data')

def get_record_list():
    """ Returns a list of all records from the relevant csv file. """
    result = []
    pm_file = open(path.abspath(DATA_DIR) + '/pm25_2009_measured.csv', 'r')
    pm_file.readline()
    for line in pm_file:
        result.append(line.strip())
    pm_file.close()
    return result

record_list = get_record_list()
random.shuffle(record_list)

def partition1():
    """ Write the shuffled records directly to a csv file. """
    result_file = open("pm_partition.csv", 'w')
    result_file.write("id,year,month,day,x,y,pm25" + "\n");
    for line in record_list:
        result_file.write(line + '\n')
    result_file.close()

def partition2():
    """ Write the partition in the form required by Kalo's implementation. """

    # build directory structure
    for i in range(1, 11):
        os.makedirs("10FoldCrossValidation/fold" + str(i))

    # intiailize file handles
    st_sample = []
    st_test = []
    value_sample = []
    value_test = []
    for i in range(1, 11):
        st_sample.append(open('10FoldCrossValidation/fold' + 
                         str(i) + '/st_sample.txt', 'w'))
        st_test.append(open('10FoldCrossValidation/fold' + 
                       str(i) + '/st_test.txt', 'w'))
        value_sample.append(open('10FoldCrossValidation/fold' + 
                            str(i) + '/value_sample.txt', 'w'))
        value_test.append(open('10FoldCrossValidation/fold' + 
                          str(i) + '/value_test.txt', 'w'))

    # write to files
    n = len(record_list) / 10
    r = len(record_list) % 10
    for i in range(10):
        if i < r:
            # TODO write n + 1 records
        else:
            # TODO write n records

    # close all file handles
    for i in range(10):
        st_sample[i].close() 
        st_test[i].close() 
        value_sample[i].close() 
        value_test[i].close() 

partition2()
