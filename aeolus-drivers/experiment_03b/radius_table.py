import csv
import pickle
import StringIO

import kdtree
import point


def load_points():
    """ Return a list of PMPoints from "data/pm25_2009_measured.csv". """

    f = open('../../data/pm25_2009_measured.csv', 'r')
    f.readline()

    def load_record(record):
        """ Parse a single CSV record. """
        result = StringIO.StringIO(record)
        fieldnames = ['site_id',
                      'year',
                      'month',
                      'day',
                      'longitude',
                      'latitude',
                      'pm25']
        reader = csv.DictReader(result, fieldnames)
        return reader.next()

    result = map(lambda rec: point.PMPoint(**rec), map(load_record, f))
    f.close()

    return result

def main():
    """ Application main. """
    

    # build point list and note its features
    point_list = load_points()
    point_list_len = len(point_list)
    percentile = int(0.80 * len(point_list))

    # build list of time_scale values
    time_scale_list = [0.001 * i for i in range(1, 25)]
    time_scale_list.extend([0.025 * i for i in range(1, 81)])

    # initialize and build radius_table
    radius_table = {}
    for time_scale in time_scale_list:
        distance_list = []
        # scale time values in point_list
        for p in point_list:
            p.scale_time(time_scale)
        # build a KDTree at that time_scale
        kdt = kdtree.KDTree(point_list)
        # find nearest neighbor for each point
        # append distance to nearest neighbor to distance_list
        for p in point_list:
            neighbor = kdt.query(p, 2)[1]
            distance_list.append(p.distance(neighbor.location))
        # sort distance_list
        distance_list.sort()
        # add correct time_scale -- distance pair to radius_table
        radius_table[str(time_scale)] = distance_list[percentile]

    # finally, pickle radius table for use in Spark cluster
    result_file = open('radius_table.pkl', 'w')
    pickle.dump(radius_table, result_file)
    result_file.close()

if __name__ == "__main__":
    main()
