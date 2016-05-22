package edu.georgiasouthern.ceit.aeolus.structures;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.gen5.api.Assertions;
import org.junit.gen5.api.Test;

/**
 * JUnit tests for the StructureService class. Here, we ensure that
 * all required CLASSPATH resources can be found and used as expected.
 *
 * @author Jason Franklin
 */
class StructureServiceTest {

    KDTree<PMPoint> dataTree;
    List<PMPoint> dataList;
    PMPoint[][] randomPartition;

    /**
     * Test that call to getDataList() in StructureService succeeds and
     * produces accurate results.
     */
    @Test
    void testGetDataList() {

        // Attempt to generate a full List<PMPoint>
        try {
            dataList = new StructureService().getDataList();
        }
        catch (IOException ex) {
            Assertions.fail("\"pm25_2009_measured.csv\" couldn't be found.");
        }

        // Ensure that all records were loaded.
        Assertions.assertEquals(146125, dataList.size());

        // Check that first element loaded accurately.
        PMPoint ptFirst = dataList.get(0);
        Assertions.assertEquals(ptFirst.get(0), -87.881412);
        Assertions.assertEquals(ptFirst.get(1), 30.498001);
        // Cannot test ptFirst.get(2) because time scale might change.
        Assertions.assertEquals(ptFirst.get(3), 14.6);

        // Check that the last element loaded accurately.
        PMPoint ptLast = dataList.get(dataList.size() - 1);
        Assertions.assertEquals(ptLast.get(0), -110.76118);
        Assertions.assertEquals(ptLast.get(1), 43.47808);
        // Cannot test ptFirst.get(2) because time scale might change.
        Assertions.assertEquals(ptLast.get(3), 12.2);
    }

    /**
     * Test that call to getDataTree() in StructureService succeeds and
     * produces accurate results.
     */
    @Test
    void testGetDataTree() {

        // Attempt to generate a full KDTree<PMPoint>.
        try {
            dataTree = new StructureService().getDataTree();
        }
        catch (IOException ex) {
            Assertions.fail("\"pm25_2009_measured.csv\" couldn't be found.");
        }

        // Ensure that all records were loaded.
        Assertions.assertEquals(146125, dataTree.size());

        // Check that the root of dataTree is the median of testList on x.
        try {
            List<PMPoint> testList = new StructureService().getDataList();
            Collections.sort(testList, (a, b) ->
                    Double.compare(a.get(0), b.get(0)));
            Assertions.assertEquals(dataTree.getRootElement(),
                    testList.get(testList.size() / 2));
        }
        catch (IOException ex) {
            Assertions.fail("\"pm25_2009_measured.csv\" couldn't be found.");
        }
    }

    /**
     * Test that call to getRandomPartition() in StructureService succeeds
     * and produces accurate results.
     */
    @Test
    void testGetRandomPartition() {

        try {
            randomPartition = new StructureService().getRandomPartition(10);
        }
        catch (IOException ex) {
            Assertions.fail("\"pm25_2009_measured.csv\" couldn't be found.");
        }
        catch (Exception ex) {
            Assertions.fail("Problem creating PMPoint partition array.");
        }

        int slots = 0;
        for (int i = 0; i < 10; i++)
            slots += randomPartition[i].length;

        Assertions.assertEquals(146125, slots);

        for (int i = 0; i < 10; i++) {
            Assertions.assertNotEquals(null, randomPartition[i][0]);
            Assertions.assertNotEquals(
                    null, randomPartition[i][randomPartition.length - 1]);
        }
    }
}
