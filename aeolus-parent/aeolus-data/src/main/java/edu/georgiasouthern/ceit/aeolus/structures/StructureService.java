package edu.georgiasouthern.ceit.aeolus.structures;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * A service class that provides methods which return structured
 * data relevant in the context of the Aeolus Project.
 *
 * @author Jason Franklin
 */
public class StructureService {

    /**
     * Returns a KDTree of PMPoint objects using the classpath
     * resource "pm25_2009_measured.csv".
     *
     * @return a KDTree containing all records from "pm25_2009_measured.csv"
     */
    public KDTree<PMPoint> getDataTree() throws IOException {

        // Allocate result
        KDTree<PMPoint> result = new KDTree<>(3);

        // Build the tree
        result.build(this.getDataList());

        // Return the result KDTree, as required
        return result;
    }

    /**
     * Returns a List of PMPoint objects using the classpath
     * resource "pm25_2009_measured.csv".
     *
     * @return a List containing all records from "pm25_2009_measured.csv"
     */
    public List<PMPoint> getDataList() throws IOException {

        // Declare result
        List<PMPoint> result = new ArrayList<>();

        // Create InputStream from CLASSPATH resource
        InputStream rStream = this.getClass().getResourceAsStream(
                "/pm25_2009_measured.csv");
        if (rStream == null)
            throw new IOException("CLASSPATH resource not found.");

        // Read and store PMPoint objects
        Scanner s = new Scanner(rStream);
        s.nextLine();
        while (s.hasNextLine())
            result.add(PMPoint.dataPoint(s.nextLine()));

        // Return result List, as required
        return result;
    }

    /**
     * Returns an array of PMPoint objects using the classpath
     * resource "pm25_2009_measured.csv".
     *
     * @return array of all available PMPoint records
     * @throws IOException
     */
    public PMPoint[] getDataArray() throws IOException {
        return (PMPoint[]) getDataList().toArray();
    }

    /**
     * Returns a two-dimensional array of PMPoint objects representing
     * a random partition of the set of all PMPoint records into k sets.
     *
     * @param k the number of sets in the resulting partition
     * @return a partition of all available PMPoint records into k sets
     */
    public PMPoint[][] getRandomPartition(int k) throws IOException {

        // declare result, a jagged array
        PMPoint[][] result = new PMPoint[k][];

        // shuffle a new List of PMPoint objects
        List<PMPoint> pointList = getDataList();
        Collections.shuffle(pointList);

        // manually specify the remaining dimensions of result
        int l = pointList.size() / k;
        int r = pointList.size() % k;
        for (int i = 0; i < k; i++)
            if (i < r)
                result[i] = new PMPoint[l + 1];
            else
                result[i] = new PMPoint[l];

        // fill result with the contents of pointList
        for (int col = 0; col < l + 1; col++)
            for (int row = 0; row < k; row++)
                if (pointList.size() > 0)
                    result[row][col] = pointList.remove(0);

        return result;
    }
}
