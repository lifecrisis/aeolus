package edu.georgiasouthern.ceit.aeolus;

import edu.georgiasouthern.ceit.aeolus.kfold.KFoldCalc;
import edu.georgiasouthern.ceit.aeolus.kfold.KFoldConf;
import edu.georgiasouthern.ceit.aeolus.structures.KDTree;
import edu.georgiasouthern.ceit.aeolus.structures.NearestNeighborList;
import edu.georgiasouthern.ceit.aeolus.structures.PMPoint;
import edu.georgiasouthern.ceit.aeolus.structures.StructureService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Generates the interpolated values for the data set "pm25_2009_measured.csv".
 * The output is in the format:
 *
 *     "original value   interpolated value"
 *
 * Model parameters are:
 *
 *     c = 0.1
 *     N = 3
 *     p = 5.0
 *
 * Created by jf on 5/24/16.
 */
public class KaloInterpolationDriver {

    public static void main(String[] args) throws IOException {
        PMPoint[][] partition = new KaloComparisonDriver().getKaloPartition();
        KFoldConf conf = new KFoldConf(10, 3, 5.0);
        System.out.println("\n\nMAE =" + new KFoldCalc().writeAndMAE(partition, conf));
    }

    /*
     * Special method for loading the partition shared with Marc Kalo.
     */
    public PMPoint[][] getKaloPartition() throws IOException {

        List<PMPoint> points = new ArrayList<>();

        // Create InputStream from CLASSPATH resource
        InputStream rStream = this.getClass().getResourceAsStream(
                "/kalo_partition.csv");
        if (rStream == null)
            throw new IOException("CLASSPATH resource not found.");

        // Read and store PMPoint objects
        Scanner s = new Scanner(rStream);
        s.nextLine();
        while (s.hasNextLine())
            points.add(PMPoint.dataPoint(s.nextLine()));

        // declare result, a jagged array
        PMPoint[][] result = new PMPoint[10][];

        // manually specify the remaining dimensions of result
        int l = points.size() / 10;
        int r = points.size() % 10;
        for (int i = 0; i < 10; i++)
            if (i < r)
                result[i] = new PMPoint[l + 1];
            else
                result[i] = new PMPoint[l];

        // fill result with the contents of pointList
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < result[row].length; col++)
                if (points.size() > 0)
                    result[row][col] = points.remove(0);
        }

        return result;
    }

}
