package edu.georgiasouthern.ceit.aeolus;

import edu.georgiasouthern.ceit.aeolus.kfold.KFoldCalc;
import edu.georgiasouthern.ceit.aeolus.kfold.KFoldConf;
import edu.georgiasouthern.ceit.aeolus.structures.PMPoint;
import edu.georgiasouthern.ceit.aeolus.structures.StructureService;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Spark driver that proves feasibility for using the cluster for
 * implementations of k-fold cross validation machine learning methods.
 *
 * @author Jason Franklin
 */
public class KFoldCrossValDriver {

    public static void main(String[] args) throws IOException {

        SparkConf sparkConf = new SparkConf().setAppName("Aeolus Project");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // set the number of folds
        int folds = 10;

        // create a List of 45 k-fold configurations
        List<KFoldConf> kFoldConfs = new ArrayList<>();
        for (int N = 3; N <= 7; N++)
            for (double P = 1.0; Math.abs(P - 5.1) > 0.05; P += 0.1)
                kFoldConfs.add(new KFoldConf(folds, N, P));

        // distribute the List of k-fold configurations to all executors
        JavaRDD<KFoldConf> kFoldConfRDD = sc.parallelize(kFoldConfs);
        kFoldConfRDD.cache();

        // generate a partition of all PMPoint records and broadcast it
        PMPoint[][] partition =
                new StructureService().getRandomPartition(folds);
        Broadcast<PMPoint[][]> broadcastPartition = sc.broadcast(partition);


        // ====================> get MARE statistics <====================

        JavaPairRDD<KFoldConf, Double> mareRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().MARE(broadcastPartition.value(), c))
        );
        mareRDD.cache();

        // print MARE results to stdout
        System.out.println("MARE Results:\n========");
        JavaRDD<String> mareResultRDD = mareRDD.map(p ->
                String.format("" + p._1().toString() + " %.7f", p._2()));
        mareResultRDD.collect().forEach(s -> System.out.println(s));

        // find the minimum and print the result
        Tuple2<KFoldConf, Double> minMARE = mareRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        System.out.println("Optimum Result (MARE): " +
                String.format("" + minMARE._1() + " %.7f", minMARE._2()));


        // ====================> get RMSPE statistics <====================

        // get RMSPE statistics
        JavaPairRDD<KFoldConf, Double> rmspeRDD = kFoldConfRDD.mapToPair(
                c -> new Tuple2<>(c,
                        new KFoldCalc().RMSPE(broadcastPartition.value(), c))
        );
        rmspeRDD.cache();

        // print RMSPE results to stdout
        System.out.println("RMSPE Results:\n=========");
        JavaRDD<String> rmspeResultRDD = rmspeRDD.map(p ->
                String.format("" + p._1().toString() + " %.7f", p._2()));
        rmspeResultRDD.collect().forEach(s -> System.out.println(s));

        // find the minimum and print the result
        Tuple2<KFoldConf, Double> minRMSPE = rmspeRDD.reduce(
                (a, b) -> (a._2() <= b._2() ? a : b));
        System.out.println("Optimum Result RMSPE: " +
                String.format("" + minRMSPE._1() + " %.7f", minRMSPE._2()));
    }
}
