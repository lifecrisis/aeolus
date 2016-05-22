package edu.georgiasouthern.ceit.aeolus;

import edu.georgiasouthern.ceit.aeolus.structures.KDTree;
import edu.georgiasouthern.ceit.aeolus.structures.PMPoint;
import edu.georgiasouthern.ceit.aeolus.structures.StructureService;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Preliminary feasibility test for adapting our spatiotemporal interpolation
 * methods using Spark.
 *
 * Created by jf on 5/1/16.
 */
public class InterpolationDriver {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf().setAppName("Interpolation Test");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // Build a KDTree and broadcast it to the executors
        KDTree<PMPoint> tree = null;
        try {
            tree = new StructureService().getDataTree();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        Broadcast<KDTree<PMPoint>> broadcastTree = sc.broadcast(tree);

        // Read centroid records from HDFS
        // note that minimum partitions is set to 14 (one per executor process!)
        JavaRDD<String> centroidRecords =
                sc.textFile("hdfs:///user/jf00936/aeolus/blkgrp_xy.csv", 14);

        // Expand centroid records into an RDD of query points (note
        // the special method reference... new in Java 8
        JavaRDD<PMPoint> queries =
                centroidRecords.flatMap(InterpolationDriver::expandRecord);

        // necessary cache() call to preserve objects in memory so that result
        // setEstimate() will persist!
        queries.cache();

        // Set the estimate for each PMPoint in queries
        queries.foreach(q ->
                q.setEstimate(broadcastTree.value().getNearestNeighbors(3, q), 5.0));

        // Write the result records to a text file in HDFS and exit
        queries.saveAsTextFile("hdfs:///user/jf00936/aeolus/blkgrp_xy_results");
    }

    // expand the records in "county_xy.csv" and "blkgrp_xy.csv" to lists of PMPoint
    // objects which include the scaled time dimension (run with "test_xy.csv" first!)
    public static List<PMPoint> expandRecord(String record) {
        List<PMPoint> result = new ArrayList<>();
        if (record.contains("id,x,y"))
            return result;
        for (int i = 1; i <= 365; i++)
            result.add(PMPoint.queryPoint(record, i));
        return result;
    }

}