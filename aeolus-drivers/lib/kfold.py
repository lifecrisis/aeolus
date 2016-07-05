"""
This module offers abstractions for performing k-fold cross validation.

Our learning method revolveds around k-fold cross validation (KFCV) with the
following parameters:

    * (k) the number of folds in our KFCV operation
    * (n) the number of nearest neighbors
    * (p) the power in the IDW-based interpolation method
    * (r) the radius, an outer bound for the nearest neighber search
    * (c) the spatiotemporal anistropy parameter (i.e. the time scale)

Some of our experiments don't use these parameters, and some do. All experiments
depend on the particular random partition generated. Thus, in more advanced
runs, we average error statistic results across partitions.
"""


class KFoldConf:
    pass


def mare(conf, point_list_brd):
    pass


def rmspe(conf, point_list_brd):
    pass
