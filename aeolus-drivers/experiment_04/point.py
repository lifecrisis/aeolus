import datetime
import csv
import math
import StringIO


class Point:

    def __init__(self, location, data):
        self.location_tuple = location
        self.data_reference = data

    def distance(self, location):
        x, y = self.location(), location
        return math.sqrt(sum([(a - b) ** 2 for a, b in zip(x, y)]))

    def data(self):
        return self.data_reference

    def location(self):
        return self.location_tuple


class PMPoint:

    def __init__(self, site_id, year, month, day, longitude, latitude, pm25):
        """ Initialize a new PMPoint object from a decoded CSV record. """
        self.site_id = site_id
        self.date = datetime.datetime(int(year), int(month), int(day))
        self.longitude = float(longitude)
        self.latitude = float(latitude)
        self.pm25 = float(pm25)

    def distance(self, location):
        """ Return the Euclidean distance between this PMPoint and location. """
        x = self.location()
        y = location
        distance = math.sqrt(sum([(a - b) ** 2 for a, b in zip(x, y)]))
        return distance

    def interpolate(self, nodes, p):
        """ Return an estimate of self.pm25 given nodes list.

        TODO: Ensure that this method returns reasonable results when used
              with utilities in kfold.py.
        """

        inv_distances = map(lambda n: (1.0 / self.distance(n.location)) ** p,
                            nodes)
        sum_inv_distances = sum(inv_distances)

        lambdas = map(lambda i: i / sum_inv_distances, inv_distances)
        result = sum(map(lambda l, n: l * n.value, lambdas, nodes))

        return result

    def location(self):
        """ Return a tuple representing the location of this PMPoint. """
        return (self.longitude, self.latitude, self.scaled_time)

    def scale_time(self, scale, start=None):
        """
        Alter the scale applied to the time dimension of this PMPoint.

        Note that this method must be called before anything useful can be
        done with this PMPoint.
        """
        if start:
            days = (self.date - start).days + 1
        else:
            days = (self.date - datetime.datetime(self.date.year, 1, 1)).days + 1
        self.scaled_time = scale * days
        # added to accommodate experiment_03a
        self.time_scale = scale
        return self

    def value(self):
        """ Return the pollution measurement recorded at this location. """
        return self.pm25

    def __str__(self):
        """ Return the string representation of this object. """
        return '< PMPoint -- ' +\
               str(self.location()) +\
               ', ' +\
               str(self.value()) +\
               '>'


def load_pm25_rdd(csv_rdd):
    """
    Return an RDD of PMPoint objects.

    The rdd argument must be an RDD of CSV records representative of PMPoint
    objects.
    """

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

    header = 'site_id,year,month,day,longitude,latitude,pm25'
    return csv_rdd.filter(lambda rec: rec != header).\
        map(load_record).\
        map(lambda rec: PMPoint(**rec))


def load_pm25_file(csv_file):
    """
    Return a list of PMPoint objects loaded directly from a CSV file.

    Rather than loading PMPoints from an RDD of CSV records, load them from
    a file directly.
    """
    f = open(csv_file, 'r')
    next(f)  # advance beyond the header

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

    result = map(lambda rec: PMPoint(**rec), map(load_record, f))
    f.close()

    return result


class QueryPoint:
    pass
