"""
Run MARE and RMSPE based learning functions.

This version of the module is adapted to use a Broadcast variable referencing
a lookup table relating time_scales to distance limits.
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


def mare(conf, point_list_brd, radius_table_brd):
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
            # The modification below was made for experiment #03B
            distance_limit = radius_table_brd.value[str(p.time_scale)]
            nnl = exclude.exclude_nodes(nnl, p, distance_limit)
            results[i] += (abs(p.interpolate(nnl, conf.power) - p.value()) /
                           p.value())
        results[i] /= len(validation_set)

    # return the average of the elements in the results vector
    return sum(results) / len(results)


def rmspe(conf, point_list_brd, radius_table_brd):
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
            # The modification below was made for experiment #03B
            distance_limit = radius_table_brd.value[str(p.time_scale)]
            nnl = exclude.exclude_nodes(nnl, p, distance_limit)
            results[i] += ((p.interpolate(nnl, conf.power) - p.value()) /
                           p.value()) ** 2.0
        results[i] /= len(validation_set)
        results[i] = math.sqrt(results[i]) * 100

    # return the average of the elements in the results vector
    return sum(results) / len(results)
