"""
"""

import datetime
import math


class PMPoint:
    """
    """

    def __init__(self, site_id, year, month, day, longitude, latitude, pm25):
        self.site_id = site_id
        self.date = datetime.datetime(int(year), int(month), int(day))
        self.longitude = float(longitude)
        self.latitude = float(latitude)
        self.pm25 = float(pm25)

    def distance(self, pack):
        """
        Return the Euclidean distance between this PMPoint and a "pack".

        Here, "pack" represents an object in the style of that returned by
        the "pack()" function below.
        """
        x = (self.longitude - pack[0]) ** 2
        y = (self.latitude - pack[1]) ** 2
        z = (self.scaled_time - pack[2]) ** 2
        distance = math.sqrt(x + y + z)
        return distance

    def interpolate(self, pack_list):
        """
        Return the estimate for pm25 at this PMPoint's location.

        Note that "pack_list" represents an iterable of packs returned from
        a nearest neighbor query.
        """
        pass

    def pack(self):
        """
        Return a compact, but useful, representation of this PMPoint.

        This method returns a tuple of the form (location_tuple, value) that
        represents the internal state of this PMPoint. Note that location_tuple
        is itself a tuple giving this PMPoint's spatiotemporal coordinates. We
        can treat the return value as a node in our kdtree or as a smaller
        serializable unit.
        """
        location_tuple = (self.longitude, self.latitude, self.scaled_time)
        value = self.pm25
        return (location_tuple, value)

    def scale_time(self, scale, start=None):
        """
        Compute and set the scaled_time attribute of this PMPoint object.

        Calculate the length of time in days from the beginning of the
        experiment's period and multiply that quantity by "scale". The datetime
        object "start" represents the beginning of the period.
        """
        if start:
            days = (self.date - start).days + 1
        else:
            days = (self.date - datetime.datetime(self.date.year, 1, 1)) + 1
        self.scaled_time = scale * days
