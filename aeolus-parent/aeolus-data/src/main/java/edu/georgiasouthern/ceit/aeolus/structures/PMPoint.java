package edu.georgiasouthern.ceit.aeolus.structures;

import java.io.Serializable;
import java.util.List;

/**
 * A class to model measurements of particulate matter located
 * at various location-time pairs in the contiguous United States
 * throughout the year of 2009.
 * <p>
 * Each PMPoint exposes a tuple of data as required by the Point
 * interface. Here, a tuple has the form:
 * <blockquote>
 * (longitude, latitude, scaled_time, pm_measurement).
 * </blockquote>
 * Here, the first three values give the spatiotemporal location of
 * the measurement (with a scaled time value), and the last value
 * represents that value of the measurement taken. 
 * <p>
 * Since some points in our problem represent points to be estimated
 * (i.e. the pm_measurement is initially unknown), we need two ways 
 * of parsing records to generate PMPoint objects. These two ways 
 * are provided in the form of "static factory methods", seen below.
 *
 * @author Jason Franklin
 */
public class PMPoint implements Point, Serializable {

    // parameter for scaling the time dimension for uniformity
    private static final double TIME_SCALE = 0.1;

    // convenience constant for calculating scaledTime
    private static final int[] DAYS = { 31, 28, 31, 30, 31, 30, 
                                        31, 31, 30, 31, 30, 31 };

    // dimension of this PMPoint
    private int dimension;

    // data for the tuple exposed by this
    private double longitude;
    private double latitude;
    private double scaledTime;
    private double pmValue;

    // Disallow instantiation if PMPoint.
    private PMPoint() {}

    /**
     * A method to parse a record from our own csv file included
     * in this project. Here we parse records representing concrete
     * measurements, i.e. these points do NOT serve as queries for 
     * our KDTree search methods.
     *
     * @param csvRecord the csv record to be parsed
     * @return a PMPoint object derived from the csvRecord argument
     */
    public static PMPoint dataPoint(String csvRecord) { 

        // allocate new PMPoint
        PMPoint p = new PMPoint();

        // split csvRecord
        String[] fields = csvRecord.split(",");

        // set dimension
        p.dimension = 3; 

        // set longitude and latitude
        p.longitude = Double.parseDouble(fields[4]);
        p.latitude = Double.parseDouble(fields[5]);

        // set scaledTime
        p.scaledTime = Double.parseDouble(fields[3]);
        for (int i = 0; i < Integer.parseInt(fields[2]) - 1; i++) {
            p.scaledTime += DAYS[i];
        }
        p.scaledTime *= TIME_SCALE;

        // set pmValue
        p.pmValue = Double.parseDouble(fields[6]);

        return p;
    }

    /**
     * A method to parse location records from our own csv files 
     * included in this project. 
     * <p>
     * For our purposes, we need to estimate values of PM<sub>2.5</sub>
     * at centroid locations of counties and census block groups within
     * the U.S. for each day throughout 2009. Our data must, therefore, 
     * be generated from the files we currently have when our program
     * is run. Thus, the day value is necessary for supplying each
     * "query point" with a scaledTime.
     *
     * @param csvRecord the csv record to be parsed 
     * @param day the day from which the scaledTime from this point will be
     *            derived
     * @return a PMPoint object requiring an estimated PM<sub>2.5</sub> value
     */
    public static PMPoint queryPoint(String csvRecord, int day) { 

        // allocate new PMPoint
        PMPoint p = new PMPoint();

        // split csvRecord
        String[] fields = csvRecord.split(","); 

        // set dimension
        p.dimension = 3; 

        // set longitude and latitude
        p.longitude = Double.parseDouble(fields[1]);
        p.latitude = Double.parseDouble(fields[2]);

        // set scaledTime
        p.scaledTime = TIME_SCALE * day;

        // set default pmValue
        p.pmValue = -1.0;

        // return reference
        return p;
    }

    /**
     * Return the value at index from the conceptual tuple of 
     * data exposed by this PMPoint. 
     * <p>
     * As a reminder, the index must be in the set {0, 1, 2, 3}, and the 
     * client of this function may expect:
     * <ol>
     *   <li>longitude</li>
     *   <li>latitude</li>
     *   <li>scaled time</li>
     *   <li>PM<sub>2.5</sub> measurement value</li>
     * </ol>
     * Note that the value at 4 will be -1.0 if this is a query point.
     *
     * @param index the index of the value to be returned
     * @return the value at index in this PMPoint's data tuple
     * @throws IndexOutOfBoundsException
     */
    public double get(int index) {
        if (index < 0 || index > getDimension())
            throw new IndexOutOfBoundsException();
        double result = 0.0;
        switch (index) { 
            case 0: result = longitude;   break;
            case 1: result = latitude;    break;
            case 2: result = scaledTime;  break;
            case 3: result = pmValue;     break;
        }
        return result;
    }

    /**
     * Return the dimension of this PMPoint object. This use case
     * sets the dimension value to 3. Be aware that the dimension does
     * not account for the measured (or estimated) value, only the Point's
     * location in hyper-space.
     *
     * @return the dimension of this PMPoint
     */
    public int getDimension() { 
        return dimension;
    }

    /**
     * Return the Euclidean distance from this PMPoint to Point p.
     *
     * @param p the other end of this Euclidean distance calculation
     * @return the distance from this to p
     * @throws IllegalArgumentException if p is not of type PMPoint
     */
    public double euclideanDistance(Point p) throws IllegalArgumentException {
        if (!(p instanceof PMPoint))
            throw new IllegalArgumentException();
        double d = 0.0;
        d += Math.pow(get(0) - p.get(0), 2);
        d += Math.pow(get(1) - p.get(1), 2);
        d += Math.pow(get(2) - p.get(2), 2);
        d = Math.sqrt(d);
        return d;
    }

    /**
     * Set the measurement value for this PMPoint. This PMPoint must 
     * have a measurement value equal to -1, which indicates that it is, 
     * in fact, a query point.
     *
     * @param pointList the list of nearest neighbors used to estimate
     *                  this PMPoint's measurement value
     * @param p the exponent that influences the weight of nearest neighbors
     *          in the interpolation process
     */
    public void setEstimate(NearestNeighborList<PMPoint> pointList, double p) {

        // get list to use Iterator feature, may be replaced later!
        List<PMPoint> pl = pointList.getList();

        // calculate sum_d over NNL, might be an error with generics...
        double sum_d = 0.0;
        for (PMPoint pt : pl)
            sum_d += Math.pow(1.0 / euclideanDistance(pt), p);

        // estimate this.pmValue
        pmValue = 0.0;
        for (PMPoint pt : pl) {
            double lambda = Math.pow(1.0 / euclideanDistance(pt), p) / sum_d;
            pmValue += lambda * pt.get(3);
        }
    }

    // convenience method for now
    public double getEstimate(NearestNeighborList<PMPoint> pointList, double p) {

        // get list to use Iterator feature, may be replaced later!
        List<PMPoint> pl = pointList.getList();

        // calculate sum_d over NNL, might be an error with generics...
        double sum_d = 0.0;
        for (PMPoint pt : pl)
            sum_d += Math.pow(1.0 / euclideanDistance(pt), p);

        // estimate this.pmValue
        double result = 0.0;
        for (PMPoint pt : pl) {
            double lambda = Math.pow(1.0 / euclideanDistance(pt), p) / sum_d;
            result += lambda * pt.get(3);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        PMPoint otherPoint = (PMPoint) o;

        if (this.dimension != otherPoint.dimension)
            return false;
        else if (Double.compare(this.longitude, otherPoint.longitude) != 0)
            return false;
        else if (Double.compare(this.latitude, otherPoint.latitude) != 0)
            return false;
        else if (Double.compare(this.scaledTime, otherPoint.scaledTime) != 0)
            return false;
        else
            return Double.compare(this.pmValue, otherPoint.pmValue) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = dimension;
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(scaledTime);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pmValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return ("(" + get(0) + ", " + get(1) + ", " + get(2) + ", " +
                get(3) + ")");
    }
}
