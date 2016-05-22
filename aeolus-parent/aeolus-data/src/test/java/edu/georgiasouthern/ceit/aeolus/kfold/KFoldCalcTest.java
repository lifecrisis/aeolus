package edu.georgiasouthern.ceit.aeolus.kfold;

import edu.georgiasouthern.ceit.aeolus.structures.PMPoint;
import edu.georgiasouthern.ceit.aeolus.structures.StructureService;

import org.junit.gen5.api.BeforeAll;
import org.junit.gen5.api.Test;

import java.io.IOException;

/**
 * Test the functionality of KFoldCalc. See that the computations
 * run and that the output of each computation is reasonable in
 * light of previous results.
 */
class KFoldCalcTest {

    static PMPoint[][] partition;

    @BeforeAll
    static void initAll() {
        try {
            partition = new StructureService().getRandomPartition(10);
        }
        catch (IOException ex) {
            System.out.println("pm25 data could not be read");
            System.exit(1);
        }
    }

    @Test
    void testMARE() {
        KFoldConf conf = new KFoldConf(10, 3, 5.0);
        System.out.println(conf);
        System.out.println("KFold.MARE(partition, conf) = " +
                new KFoldCalc().MARE(partition, conf));
    }

    @Test
    void testRMSPE() {
        KFoldConf conf = new KFoldConf(10, 3, 5.0);
        System.out.println(conf);
        System.out.println("KFold.RMSPE(partition, conf) = " +
                new KFoldCalc().RMSPE(partition, conf));
    }
}
