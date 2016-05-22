package edu.georgiasouthern.ceit.aeolus.structures;

/**
 * An interface for data points occurring within the interpolation 
 * problem domain explored in this project. 
 * <p>
 * In our case, points are usually generated from csv records, with
 * each new file of records having a unique format. For this reason, 
 * new types of points must be parsed into objects that expose certain
 * necessary data and operations. The key idea is that what is meant by
 * "point" needs to be refined <i>here</i> so that other data structures
 * that are critical to our project's operation don't have to modify 
 * their behavior to accommodate a new csv file format.
 * <p>
 * To use the KDTree structure along with all of it's search methods, simply
 * parse your data into objects of a type that implements this interface!
 */
public interface Point {

    /**
     * Calculate the distance between this Point and Point p. 
     *
     * @param p the other end of the distance calculation
     * @return the Euclidean distance from this Point to p
     */
    double euclideanDistance(Point p); 

    /**
     * Return the value at index in the tuple of data exposed by this 
     * Point object. 
     * <p>
     * Each Point must expose its relevant data. In most
     * cases, this includes (in the first k indices) a location in
     * k-dimensional space and (in the remaining slots) a value representing
     * some sort of measurement of interest. All of this data is accessible
     * through this method.
     *
     * @param index the index of the piece of data to be accessed
     * @return the numerical value at the specified index
     */
    double get(int index);

    /**
     * Return the dimension of this Point. 
     * <p>
     * Since our problem involves values located at points in some 
     * hyperspace, we need to be able to verify that Points used 
     * together belong in the same space. 
     *
     * @return the integer dimension of the space in which this point belongs
     */
    int getDimension();
}
