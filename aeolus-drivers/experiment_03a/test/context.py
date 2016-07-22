import os
import sys


# Modify the module search path to begin with the parent directory of this
# directory (i.e. "test/").
sys.path.insert(0, os.path.abspath('..'))

import kdtree
import point
