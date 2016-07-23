import os
import sys

# TEST_ROOT = os.path.dirname(os.path.abspath(__file__))
# PROJECT_ROOT = os.path.dirname(TEST_ROOT)
# 
# # temporarily modify the module search path to include the project root
# sys.path.insert(0, PROJECT_ROOT)
# 
# add import statements for modules serving as test subjects here... they
# can be imported from this module by other modules in this test/ package
import kdtree
import point

# undo the change to sys.path (not really necessary since this change only
# occurs in testing...
sys.path = sys.path[1:]
