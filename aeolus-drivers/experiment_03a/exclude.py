"""
This module provides the "exclude_nodes()" method which is responsible for
filtering nearest neighbor results returned from "KDTree.query()".

The idea is to prove the feasability of making accuracy improvements in our
interpolation methods by filtering outliers in a naive way prior to computing
our estimation.
"""

import math


def exclude_nodes(node_list, query_point, day_limit, distance_limit):
    """
    Docstring.
    """

    result = node_list

    # get relevant data from query_point
    x_loc = (query_point.longitude, query_point.latitude)
    x_time = query_point.scaled_time

    for i, node in enumerate(node_list):

        # get data from node in node_list
        y_loc = node.location[:2]
        y_time = node.location[2]

        # check distance
        distance = math.sqrt(sum([(a - b) ** 2 for a, b in zip(x_loc, y_loc)]))
        if distance > distance_limit:
            if i == 0:
                result = node_list[:1]
            else:
                result = node_list[:i]
            break

        # check time difference
        time_diff = abs(x_time - y_time) / query_point.time_scale
        if time_diff > day_limit:
            result = node_list[:i]
            break

    return result
