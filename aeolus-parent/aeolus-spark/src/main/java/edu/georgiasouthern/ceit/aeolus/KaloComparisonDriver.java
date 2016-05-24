package edu.georgiasouthern.ceit.aeolus;

import edu.georgiasouthern.ceit.aeolus.kfold.KFoldCalc;
import edu.georgiasouthern.ceit.aeolus.kfold.KFoldConf;
import edu.georgiasouthern.ceit.aeolus.structures.PMPoint;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Error statistic comparison test between SF-based and IDW-based
 * spatiotemporal interpolation. The results here are compared with the
 * work of Marc Kalo.
 *
 * This driver should be run multiple times, i.e. once for each
 * c value.
 *
 * Created by jf on 5/22/16.
 */
public class KaloComparisonDriver {

    public static void main(String[] args) throws IOException {

        SparkConf sparkConf = new SparkConf().setAppName("Kalo Comparison");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // generate list of configurations to test
        List<KFoldConf> kFoldConfs = new ArrayList<>();
        int[] N = {3, 4, 5, 6, 7};
        double[] p = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 9; j++)
                kFoldConfs.add(new KFoldConf(10, N[i], p[j]));

        // distribute the List of k-fold configurations to all executors
        JavaRDD<KFoldConf> kFoldConfRDD = sc.parallelize(kFoldConfs);
        kFoldConfRDD.cache();

        // retrieve partition from classpath and broadcast it
        PMPoint[][] partition = new KaloComparisonDriver().getKaloPartition();
        Broadcast<PMPoint[][]> broadcastPartition = sc.broadcast(partition);

        // build result String to summarize error statistics
        String result = "Error statistics for \"c = 0.1086\":\n";


        // ====================> get MAE statistic <====================

        JavaPairRDD<KFoldConf, Double> maeRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().MAE(broadcastPartition.value(), c))
        );
        maeRDD.cache();

        // find the minimum and print the result
        Tuple2<KFoldConf, Double> minMAE = maeRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        result += ("Optimum Result (MAE):\t" +
                String.format("" + minMAE._1() + " %.7f\t", minMAE._2()) + "\n");
        maeRDD.unpersist();


        // ====================> get MSE statistic <====================

        JavaPairRDD<KFoldConf, Double> mseRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().MSE(broadcastPartition.value(), c))
        );
        mseRDD.cache();

        // find the minimum and print the result
        Tuple2<KFoldConf, Double> minMSE = mseRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        result += ("Optimum Result (MSE):\t" +
                String.format("" + minMSE._1() + " %.7f\t", minMSE._2()) + "\n");
        mseRDD.unpersist();


        // ====================> get RMSE statistic <====================

        JavaPairRDD<KFoldConf, Double> rmseRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().RMSE(broadcastPartition.value(), c))
        );
        rmseRDD.cache();

        // find the minimum and print the result
        Tuple2<KFoldConf, Double> minRMSE = rmseRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        result += ("Optimum Result (RMSE):\t" +
                String.format("" + minRMSE._1() + " %.7f\t", minRMSE._2()) + "\n");
        rmseRDD.unpersist();


        // ====================> get MARE statistic <====================

        JavaPairRDD<KFoldConf, Double> mareRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().MARE(broadcastPartition.value(), c))
        );
        mareRDD.cache();

        // print all elements of the
        System.out.println("MARE Results:");
        mareRDD.collect().forEach(x -> System.out.println(x._1().toString() + x._2()));

        // find the minimum and add to result
        Tuple2<KFoldConf, Double> minMARE = mareRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        result += ("Optimum Result (MARE):\t" +
                String.format("" + minMARE._1() + " %.7f\t", minMARE._2()) + "\n");
        mareRDD.unpersist();

        // Show result as one block
        System.out.print(result);

        // ====================> get CVRS statistic <====================

        JavaPairRDD<KFoldConf, Double> cvrsRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().CVRS(broadcastPartition.value(), c))
        );
        cvrsRDD.cache();

        // print all elements of the
        System.out.println("CVRS Results:");
        cvrsRDD.collect().forEach(x -> System.out.println(x._1().toString() + x._2()));

        // print all elements of the
        // find the maximum for all configurations and print the result
        Tuple2<KFoldConf, Double> maxCVRS = cvrsRDD.reduce(
                (a, b) -> (a._2() >= b._2() ? a : b));
        result += ("Optimum Result (CVRS):\t" +
                String.format("" + maxCVRS._1() + " %.7f\t", maxCVRS._2()) + "\n");
        cvrsRDD.unpersist();

        // Show result as one block
        System.out.print(result);
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
