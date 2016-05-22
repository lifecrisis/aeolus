package edu.georgiasouthern.ceit.aeolus.kfold;

import java.io.Serializable;

/**
 * Type encapsulating the configuration of a given KFold
 * computation.
 *
 * @author Jason Franklin
 */
public class KFoldConf implements Serializable {

    private final int FOLDS;
    private final int NEIGHBORS;
    private final double POWER;

    public KFoldConf(int folds, int neighbors, double power) {
        FOLDS = folds;
        NEIGHBORS = neighbors;
        POWER = power;
    }

    public int getFOLDS() {
        return FOLDS;
    }

    public int getNEIGHBORS() {
        return NEIGHBORS;
    }

    public double getPOWER() {
        return POWER;
    }

    @Override
    public String toString() {
        return String.format("(F=%d, N=%d, P=%.2f)",
                FOLDS, NEIGHBORS, POWER);
    }
}
