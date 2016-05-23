package edu.georgiasouthern.ceit.aeolus.kfold;

import edu.georgiasouthern.ceit.aeolus.structures.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class that performs k-fold cross validations which generate
 * average MARE and RMSPE error statistics for a given partition and
 * KFoldConf configuration object.
 *
 * @author Jason Franklin
 */
public class KFoldCalc {

    /*
     * Mean Absolute Error
     */
    public double MAE(PMPoint[][] partition, KFoldConf conf) {

        double[] results = new double[conf.getFOLDS()];

        for (int i = 0; i < results.length; i++) {

            // single out validationSet and trainingSet
            List<PMPoint> validationSet = Arrays.asList(partition[i]);
            List<PMPoint> trainingSet = new ArrayList<>();

            // join sets into trainingSet, as required
            for (int j = 0; j < partition.length; j++)
                if (j != i)
                    trainingSet.addAll(Arrays.asList(partition[j]));

            // build a KDTree from trainingSet
            KDTree<PMPoint> kdtree = new KDTree<>(3);
            kdtree.build(trainingSet);

            // compute result for this validation set
            for (PMPoint p : validationSet) {
                NearestNeighborList<PMPoint> nnl =
                        kdtree.getNearestNeighbors(conf.getNEIGHBORS(), p);
                results[i] += Math.abs(p.getEstimate(nnl, conf.getPOWER()) -
                        p.get(3));
            }
            results[i] /= validationSet.size();
        }

        // average the results for each set in the partition
        double result = 0.0;
        for (double r : results) result += r;
        result /= results.length;

        // return average
        return result;
    }

    /*
     * Mean Absolute Relative Error
     */
    public double MARE(PMPoint[][] partition, KFoldConf conf) {

        double[] results = new double[conf.getFOLDS()];

        for (int i = 0; i < results.length; i++) {

            // single out validationSet and trainingSet
            List<PMPoint> validationSet = Arrays.asList(partition[i]);
            List<PMPoint> trainingSet = new ArrayList<>();

            // join sets into trainingSet, as required
            for (int j = 0; j < partition.length; j++)
                if (j != i)
                    trainingSet.addAll(Arrays.asList(partition[j]));

            // build a KDTree from trainingSet
            KDTree<PMPoint> kdtree = new KDTree<>(3);
            kdtree.build(trainingSet);

            // compute result for this validation set
            for (PMPoint p : validationSet) {
                NearestNeighborList<PMPoint> nnl =
                        kdtree.getNearestNeighbors(conf.getNEIGHBORS(), p);
                results[i] += (Math.abs(p.getEstimate(nnl, conf.getPOWER()) -
                        p.get(3)) / p.get(3));
            }
            results[i] /= validationSet.size();
        }

        // average the results for each set in the partition
        double result = 0.0;
        for (double r : results) result += r;
        result /= results.length;

        // return average
        return result;
    }

    /*
     * Mean Square Error
     */
    public double MSE(PMPoint[][] partition, KFoldConf conf) {

        double[] results = new double[conf.getFOLDS()];

        for (int i = 0; i < results.length; i++) {

            // single out validationSet and trainingSet
            List<PMPoint> validationSet = Arrays.asList(partition[i]);
            List<PMPoint> trainingSet = new ArrayList<>();

            // join sets into trainingSet, as required
            for (int j = 0; j < partition.length; j++)
                if (j != i)
                    trainingSet.addAll(Arrays.asList(partition[j]));

            // build a KDTree from trainingSet
            KDTree<PMPoint> kdtree = new KDTree<>(3);
            kdtree.build(trainingSet);

            // compute result for this validation set
            for (PMPoint p : validationSet) {
                NearestNeighborList<PMPoint> nnl =
                        kdtree.getNearestNeighbors(conf.getNEIGHBORS(), p);
                results[i] += Math.pow(p.getEstimate(nnl, conf.getPOWER()) -
                        p.get(3), 2);
            }
            results[i] /= validationSet.size();
        }

        // average the results for each set in the partition
        double result = 0.0;
        for (double r : results) result += r;
        result /= results.length;

        // return average
        return result;
    }

    /*
     * Root Mean Squared Error
     */
    public double RMSE(PMPoint[][] partition, KFoldConf conf) {

        double[] results = new double[conf.getFOLDS()];

        for (int i = 0; i < results.length; i++) {

            // single out validationSet and trainingSet
            List<PMPoint> validationSet = Arrays.asList(partition[i]);
            List<PMPoint> trainingSet = new ArrayList<>();

            // join sets into trainingSet, as required
            for (int j = 0; j < partition.length; j++)
                if (j != i)
                    trainingSet.addAll(Arrays.asList(partition[j]));

            // build a KDTree from trainingSet
            KDTree<PMPoint> kdtree = new KDTree<>(3);
            kdtree.build(trainingSet);

            // compute result
            for (PMPoint p : validationSet) {
                NearestNeighborList<PMPoint> nnl =
                        kdtree.getNearestNeighbors(conf.getNEIGHBORS(), p);
                results[i] +=
                        Math.pow(p.getEstimate(nnl, conf.getPOWER()) - p.get(3), 2.0);
            }
            results[i] /= validationSet.size();
            results[i] = Math.sqrt(results[i]);
        }

        double result = 0.0;
        for (double r : results) result += r;
        result /= results.length;

        return result;
    }

    /*
     * Root Mean Squared Percentage Error
     */
    public double RMSPE(PMPoint[][] partition, KFoldConf conf) {

        double[] results = new double[conf.getFOLDS()];

        for (int i = 0; i < results.length; i++) {

            // single out validationSet and trainingSet
            List<PMPoint> validationSet = Arrays.asList(partition[i]);
            List<PMPoint> trainingSet = new ArrayList<>();

            // join sets into trainingSet, as required
            for (int j = 0; j < partition.length; j++)
                if (j != i)
                    trainingSet.addAll(Arrays.asList(partition[j]));

            // build a KDTree from trainingSet
            KDTree<PMPoint> kdtree = new KDTree<>(3);
            kdtree.build(trainingSet);

            // compute result
            for (PMPoint p : validationSet) {
                NearestNeighborList<PMPoint> nnl =
                        kdtree.getNearestNeighbors(conf.getNEIGHBORS(), p);
                results[i] +=
                        Math.pow((p.getEstimate(nnl, conf.getPOWER()) - p.get(3)) /
                                p.get(3), 2.0);
            }
            results[i] /= validationSet.size();
            results[i] = Math.sqrt(results[i]);
            results[i] *= 100;
        }

        double result = 0.0;
        for (double r : results) result += r;
        result /= results.length;

        return result;
    }

    /*
     * Cross Validation R-squared statistic.
     */
    public double CVR2(PMPoint[][] partition, KFoldConf conf) {
        // TODO: implement this STUB
        return 0.0;
    }

    /*
     * Private helper for CVR2. Returns the MSE for all observed values in row
     * "i" of the given partition.
     */
    private double observedMSE(PMPoint[][] partition, int i) {

        // declare and initialize result
        double result = 0.0;

        // calculate the average of the observed values in row i
        double mean;
        double sum = 0.0;
        for (PMPoint p: partition[i])
            sum += p.get(3);
        mean = sum / partition[i].length;

        // use the sum of the squared differences to set result
        double diffSum = 0.0;
        for (PMPoint p: partition[i])
            diffSum += Math.pow(p.get(3) - mean, 2);
        result = diffSum / partition[i].length;

        // return result
        return result;
    }
}