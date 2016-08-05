"""
Run MARE and RMSPE based learning functions.

Modified for experiment #4. Bagging is used here with the following settings:
    1. 3 "bags", each 75% of the training set, results averaged
    2. 3 "bags", each 50% of the training set, results averaged
These settings may (in fact, probably) will lead to a significantly increased
runtime on our Spark cluster, but it is worth it in order to establish a
baseline expectation for accuracy decreases in future projects that use this
technique.
"""

import copy
import math
import random

import kdtree


class KFoldConf:

    def __init__(self, folds, neighbors, power, radius, time_scale, alpha, bags):
        self.folds = folds
        self.neighbors = neighbors
        self.power = power
        self.radius = radius
        self.time_scale = time_scale
        # alpha value from bagging
        self.alpha = alpha
        # m value from bagging
        self.m = bags

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


def sample_with_replacement(population, k):
    """
    Return a list of size k sampled randomly (with replacement) from
    iterable.
    """
    n = len(population)
    _random, _int = random.random, int  # speed hack 
    result = [None] * k
    for i in xrange(k):
        j = _int(_random() * n)
        result[i] = population[j]
    return result


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

    # generate results for kfold cross validation with this err stat
    results = [0.0 for i in range(conf.folds)]
    for i in range(conf.folds):

        # initialize validation set and training set
        validation_set = partition[i]
        training_set = list()
        for j in range(conf.folds):
            if j != i:
                training_set += partition[j]

        # generate conf.m bags at conf.alpha by sampling with replacement
        n_prime = int(len(training_set) * conf.alpha)
        bags = [sample_with_replacement(training_set, n_prime)
                for i in range(conf.m)]

        # TODO(jf): build 3 kdtrees, one for each bag (issue_number)
        # for each bag, compute bag_result
        for bag in bags:

            # build a KDTree from the bag
            tree = kdtree.KDTree(bag)

            # compute result for the validation set
            bag_result = 0.0
            for p in validation_set:
                nnl = tree.query(p, conf.neighbors)
                # TODO(jf): average interpolation values... not error statistics!!! (issue_number)
                bag_result += (abs(p.interpolate(nnl, conf.power) - p.value()) /
                               p.value())
            bag_result /= len(validation_set)
            results[i] += bag_result

        # average results[i] across conf.m (i.e. number of bags)
        results[i] /= conf.m

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

    # generate results for kfold cross validation with this err stat
    results = [0.0 for i in range(conf.folds)]
    for i in range(conf.folds):

        # initialize validation set and training set
        validation_set = partition[i]
        training_set = list()
        for j in range(conf.folds):
            if j != i:
                training_set += partition[j]

        # generate conf.m bags at conf.alpha by sampling with replacement
        n_prime = int(len(training_set) * conf.alpha)
        bags = [sample_with_replacement(training_set, n_prime)
                for i in range(conf.m)]

        # for each bag, compute bag_result
        for bag in bags:

            # build a KDTree from the bag
            tree = kdtree.KDTree(bag)

            # compute result for this validation set
            bag_result = 0.0
            for p in validation_set:
                nnl = tree.query(p, conf.neighbors)
                bag_result += ((p.interpolate(nnl, conf.power) - p.value()) /
                               p.value()) ** 2.0
            bag_result /= len(validation_set)
            bag_result = math.sqrt(bag_result) * 100
            results[i] += bag_result

        # average results[i] across conf.m (i.e. number of bags)
        results[i] /= conf.m

    # return the average of the elements in the results vector
    return sum(results) / len(results)
