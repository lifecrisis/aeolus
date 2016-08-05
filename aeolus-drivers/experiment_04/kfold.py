"""
Run MARE and RMSPE based learning functions.

Modified for experiment #4. Bagging is used here with the following settings:
    1. 3 "bags", each 80% of the training set, results averaged
    2. 3 "bags", each 40% of the training set, results averaged
These settings may (in fact, probably) will lead to a significantly increased
runtime on our Spark cluster, but it is worth it in order to establish a
baseline expectation for accuracy decreases in future projects that use this
technique.
"""

import copy
import math

import exclude
import kdtree


class KFoldConf:

    def __init__(self, folds, neighbors, power, radius, time_scale):
        self.folds = folds
        self.neighbors = neighbors
        self.power = power
        self.radius = radius
        self.time_scale = time_scale

    def __repr__(self):
        return ('KFoldConf(folds=' +
                repr(self.folds) +
                ', neighbors=' +
                repr(self.neighbors) +
                ', power=' +
                repr(self.power) +
                ', radius=' +
                repr(self.radius) +
                ', time_scale=' +
                repr(self.time_scale) +
                ')')


def sample_with_replacement(iterable, n):
    """
    Return a list of size n sampled randomly (with replacement) from
    iterable.
    """
    pass


def mare(conf, point_list_brd):
    """
    Return the MARE error statistic generated from K-fold cross validation.

    Take the given KFoldConf object and an ordered broadcasted list of point
    objects and return the desired error statistic.
    """

    # deep copy point_list
    points = copy.deepcopy(point_list_brd.value)

    # scale time dimensions
    for p in points:
        p.scale_time(conf.time_scale)

    # build a list of sets representing the relevant partition
    partition = [list() for i in range(conf.folds)]
    for i, p in enumerate(points):
        partition[i % conf.folds].append(p)

    # TODO(jf): Run this loop for conf.m and conf.alpha. (iss3)
    results = [0.0 for i in range(conf.folds)]
    for i in range(conf.folds):
        # initialize validation set and training set
        validation_set = partition[i]
        training_set = list()
        for j in range(conf.folds):
            if j != i:
                training_set += partition[j]
        # build a kdtree from the training set
        tree = kdtree.KDTree(training_set)
        # compute result for this validation set
        for p in validation_set:
            nnl = tree.query(p, conf.neighbors)
            results[i] += (abs(p.interpolate(nnl, conf.power) - p.value()) /
                           p.value())
        results[i] /= len(validation_set)

    # return the average of the elements in the results vector
    return sum(results) / len(results)


def rmspe(conf, point_list_brd):
    """
    Return the RMSPE error statistic generated from K-fold cross validation.

    Take the given KFoldConf object and an ordered broadcasted list of point
    objects and return the desired error statistic.
    """

    # deep copy point_list
    points = copy.deepcopy(point_list_brd.value)

    # scale time dimensions
    for p in points:
        p.scale_time(conf.time_scale)

    # build a list of sets representing the relevant partition
    partition = [list() for i in range(conf.folds)]
    for i, p in enumerate(points):
        partition[i % conf.folds].append(p)

    # TODO(jf): Run this loop for conf.m and conf.alpha. (iss3)
    results = [0.0 for i in range(conf.folds)]
    for i in range(conf.folds):
        # initialize validation set and training set
        validation_set = partition[i]
        training_set = list()
        for j in range(conf.folds):
            if j != i:
                training_set += partition[j]
        # build a kdtree from the training set
        tree = kdtree.KDTree(training_set)
        # compute result for this validation set
        for p in validation_set:
            nnl = tree.query(p, conf.neighbors)
            results[i] += ((p.interpolate(nnl, conf.power) - p.value()) /
                           p.value()) ** 2.0
        results[i] /= len(validation_set)
        results[i] = math.sqrt(results[i]) * 100

    # return the average of the elements in the results vector
    return sum(results) / len(results)
