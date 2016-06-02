#! /usr/bin/env python

import math


SF_FILES = [
    open("PM2.5/PM2.5/outlier_removed_1.txt"),
    open("PM2.5/PM2.5/outlier_removed_01.txt"),
    open("PM2.5/PM2.5/outlier_removed_02.txt"),
    open("PM2.5/PM2.5/outlier_removed_00667.txt"),
]

IDW_FILE = open("outlier_removed_idw.txt")


def mse(values):
    """ Computes the Mean Square Error statistic for an iterable of
        floating-point values. """

    N = len(values)

    mean = sum(values) / N
    mse = 0.0
    for val in values:
        mse += (val - mean) ** 2
    mse = mse / N

    return mse



def sf_r_square(sf_file):
    """ Returns the R-squared statistic for the SF-based interpolation 
        results in the given file. """

    result = 0.0

    observed = []
    interpolated = []

    sf_file.readline()
    for line in sf_file:
        line = line.split()
        observed.append(float(line[1]))
        interpolated.append(float(line[2]))

    N = len(observed)

    sq_diffs = []
    for i in range(N):
        sq_diffs.append((observed[i] - interpolated[i]) ** 2)

    MSE = sum(sq_diffs) / N
    RMSE = math.sqrt(MSE)
    MSE_OBS = mse(observed)

    return max(0, 1 - ((RMSE ** 2) / MSE_OBS))

        
def idw_r_square(idw_file):
    """ Returns the R-squared statistic for the IDW-based interpolation 
        results in the given file. """

    result = 0.0

    observed = []
    interpolated = []

    idw_file.readline()
    for line in idw_file:
        line = line.split()
        observed.append(float(line[0]))
        interpolated.append(float(line[1]))

    N = len(observed)

    sq_diffs = []
    for i in range(N):
        sq_diffs.append((observed[i] - interpolated[i]) ** 2)

    MSE = sum(sq_diffs) / N
    RMSE = math.sqrt(MSE)
    MSE_OBS = mse(observed)

    return max(0, 1 - ((RMSE ** 2) / MSE_OBS))


for f in SF_FILES:
    print "%s\tr^2 = %.7f" % (f.name.split('/')[2], sf_r_square(f))
    f.close()

print "%s\tr^2 = %.7f" % (IDW_FILE.name, idw_r_square(IDW_FILE))
IDW_FILE.close()
