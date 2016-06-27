from point import PMPoint
import kdtree

POINT_LIST = [PMPoint('test', 2000, 1, 4, 3, 1, 5.0),
              PMPoint('test', 2000, 1, 7, 2, 3, 5.0),
              PMPoint('test', 2000, 1, 4, 4, 3, 5.0),
              PMPoint('test', 2000, 1, 3, 2, 1, 5.0),
              PMPoint('test', 2000, 1, 5, 2, 4, 5.0),
              PMPoint('test', 2000, 1, 4, 6, 1, 5.0),
              PMPoint('test', 2000, 1, 4, 1, 4, 5.0),
              PMPoint('test', 2000, 1, 7, 0, 5, 5.0),
              PMPoint('test', 2000, 1, 5, 5, 2, 5.0),
              PMPoint('test', 2000, 1, 6, 4, 0, 5.0),
              PMPoint('test', 2000, 1, 6, 7, 1, 5.0)]


class Test:
    def __init__(self, location, value):
        self.t = location
        self.v = value

    def location(self):
        return self.t

    def value(self):
        return self.v


T = [Test((7, 2), 1),
     Test((2, 3), 1),
     Test((5, 4), 1),
     Test((9, 6), 1),
     Test((4, 7), 1),
     Test((8, 1), 1)]
