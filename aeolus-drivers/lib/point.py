"""
"""

import datetime


class PMPoint:
    """
    """

    def __init__(self, site_id, year, month, day, longitude, latitude, pm25):
        self.site_id = site_id
        self.date = datetime.datetime(int(year), int(month), int(day))
        self.latitude = float(latitude)
        self.longitude = float(longitude)
        self.pm25 = float(pm25)
