#! /usr/bin/env python

# "partition.py": A script that gives two random partitions of the records in 
# the file "data/pm25_2009_measured.csv". This serves two purposes: 
#     (1) It allows Marc Kalo and I to use the same partition to compare our 
#         estimation procedures, resolving a format discrepancy. 
#     (2) It replaces the invalid partition that he was using in his own 
#         tests.

from os import path

import datetime
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
        result.append(line)
    pm_file.close()
    return result

record_list = get_record_list()
random.shuffle(record_list)

def partition1():
    """ Write the shuffled records directly to a csv file. """
    result_file = open("kalo_partition.csv", 'w')
    result_file.write("id,year,month,day,x,y,pm25" + "\n");
    for line in record_list:
        result_file.write(line)
    result_file.close()

# replace all commas in record_list with tabs, to accomodate Kalo
for line in record_list:
    line.replace(',', '\t')

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

    def write_line(line, fold_number, sample):
        """ Helper to process a line and write its data to relevant files. """
        line = line.strip().split(',')
        x = line[4]
        y = line[5]
        day = datetime.date(int(line[1]),
                            int(line[2]),
                            int(line[3])).strftime("%j").lstrip('0')
        value = line[6]
        if sample:
            st_sample[fold_number].write(x + '\t' + y + '\t' + day + '.0\n')
            value_sample[fold_number].write(value + '\n')
        else:
            st_test[fold_number].write(x + '\t' + y + '\t' + day + '.0\n')
            value_test[fold_number].write(value + '\n')

    # write to files
    n = len(record_list) / 10
    r = len(record_list) % 10
    for i in range(10):
        if i < r:
            # count up in increments of n + 1
            start = i * (n + 1)
            end = start + (n + 1)
        else:
            # count up in increments of n
            start = r * (n + 1) + (i - r) * n 
            end = start + n
        for line in record_list[:start]:
            write_line(line, i, True)
        for line in record_list[start:end]:
            write_line(line, i, False)
        for line in record_list[end:]:
            write_line(line, i, True)

    # close all file handles
    for i in range(10):
        st_sample[i].close() 
        st_test[i].close() 
        value_sample[i].close() 
        value_test[i].close() 

# perform all necessary writes and exit
partition1()
partition2()
