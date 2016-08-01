"""
This module provides the "exclude_nodes()" method which is responsible for
filtering nearest neighbor results returned from "KDTree.query()".

Here, we provide a modified implementation of this module (compared to the
one found in "experiment_03a/". The exclude() method found here will filter
a result list based on 3-D Euclidean distance.
"""


def exclude_nodes(node_list, query_point, distance_limit):
    """
    Filter node_list, removing nodes further than distance_limit from
    query_point.
    """
    result = node_list
    for i, node in enumerate(node_list):
        distance = query_point.distance(node.location)
        if distance > distance_limit:
            if i == 0:
                result = result[:1]
            else:
                result = result[:i]
            break
    return result
