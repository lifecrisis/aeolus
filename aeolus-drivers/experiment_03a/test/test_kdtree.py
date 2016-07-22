"""
Unit test module for temporary modifications to "kdtree.py".
"""

import os.path
import random
import unittest

from context import kdtree
from context import point


class KDTreeModTestCase(unittest.TestCase):
    """
    This TestCase examines the the KDTree query() method for consistency in
    light of modifications made to the class specifically for this experiment.
    """

    def setUp(self):
        """ Build "self.pm_point_list" and "self.pm_point_tree". """

        # load PMPoint objects from file
        path = os.path.abspath(__file__)
        path = path[:path.rfind('aeolus/') + len('aeolus/')]
        csv_file_name = os.path.join(path, 'data/pm25_2009_measured.csv')
        pm_point_list = point.load_pm25_file(csv_file_name)

        # create structures used in this TestCase
        self.pm_point_list = random.sample(pm_point_list, 100)
        for p in self.pm_point_list:
            p.scale_time(1)
        self.pm_point_tree = kdtree.KDTree(self.pm_point_list)

    def tearDown(self):
        """
        Delete the data structures initialized in "setUp()". 

        It really is not necessary to do this, but I want the practice of
        writing a simple tearDown() function.
        """
        del self.pm_point_list
        del self.pm_point_tree

    def test_query_accuracy(self):
        """ Docstring. """
        self.fail("unimplemented test")

    def test_query_nodes_only_true(self):
        """ Doctring. """
        self.fail("unimplemented test")

    def test_query_nodes_only_false(self):
        """ Doctring. """
        self.fail("unimplemented test")


if __name__ == "__main__":
    suite = unittest.TestLoader().loadTestsFromTestCase(KDTreeModTestCase)
    unittest.TextTestRunner(verbosity=2).run(suite)
