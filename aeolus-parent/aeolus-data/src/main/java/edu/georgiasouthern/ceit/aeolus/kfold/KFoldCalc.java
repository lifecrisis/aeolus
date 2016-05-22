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
     * Cross Validation R-squared
     */
//    public double CVR2(PMPoint[][] partition, KFoldConf conf) {
//        // TODO: verify CVR2() implementation
//        KFoldCalc calc = new KFoldCalc();
//        return Math.max(0,
//                   1 - (Math.pow(calc.RMSE(partition, conf), 2.0) /
//                           calc.observedMSE(partition))
//               );
//    }

    /*
     * Private helper for CVR2. Returns the MSE for All observed values in the partition.
     */
//    private double observedMSE(PMPoint[][] partition) {
//        // TODO: verify observedMSE() implementation
//        return 1.0;
//    }
}