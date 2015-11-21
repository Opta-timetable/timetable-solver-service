package org.kollavarsham.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.io.File;

import org.kollavarsham.utils.SpecManager;

/**
 * JUnit tests for SpecManager utilities
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpecManagerTest {

    private static String specId;
    private static String BASE_PATH = "./data/curriculumcourse/";

    @BeforeClass
    public static void setUp() throws Exception {
        specId = UUID.randomUUID().toString();

        File specFile = new File( BASE_PATH + specId + "/unsolved/test.xml");
        specFile.getParentFile().mkdirs();
        specFile.createNewFile();

        File solutionFile = new File( BASE_PATH + specId + "/solved/solution.xml");
        solutionFile.getParentFile().mkdirs();
        solutionFile.createNewFile();
    }

    @Test
    public void test1GetSolutionFilePath() throws Exception {
        assertEquals("getSolutionFilePath is as expected for a valid specId", BASE_PATH + specId + "/solved/solution.xml",
                SpecManager.getSolutionFilePath(specId));
        assertEquals("getSolutionFilePath returns empty string for an invalid specId", "",
                SpecManager.getSolutionFilePath(specId+"I_make_it_invalid"));
    }

    @Test
    public void test2GetSpecFilePath() throws Exception {
        assertEquals("getSpecFilePath is as expected for a valid specId", BASE_PATH + specId + "/unsolved/test.xml",
                SpecManager.getSpecFilePath(specId));
        assertEquals("getSpecFilePath returns empty string for an invalid specId", "",
                SpecManager.getSpecFilePath(specId + "I_make_it_invalid"));
    }

    @Test
    public void test3GetSolutionFileBasePath() throws Exception {
        assertEquals("getSolutionFileBasePath is as expected for a valid specId", BASE_PATH + specId + "/solved/",
                SpecManager.getSolutionFileBasePath(specId));
    }

    @Test
    public void test4GetSpecFileBasePath() throws Exception {
        assertEquals("getSpecFileBasePath is as expected for a valid specId", BASE_PATH + specId + "/unsolved/",
                SpecManager.getSpecFileBasePath(specId));
    }
      
    @Test
    public void test5DoesSpecFileExist() throws Exception {
        assertTrue("doesSpecFileExist returns True for a valid specId",
                SpecManager.doesSpecFileExist(specId));
        assertFalse("doesSpecFileExist returns False for an invalid specId",
                SpecManager.doesSpecFileExist(specId + "I_make_it_invalid"));
    }

    @Test
    public void test6RemoveSpecFiles() throws Exception {
        assertTrue("RemoveSpecFiles should return True for a correct specID", SpecManager.removeSpecFiles(specId));
        assertFalse("RemoveSpecFiles should return False for an incorrect specID",
                SpecManager.removeSpecFiles(specId + "I_make_it_invalid"));
        assertFalse("RemoveSpecFiles should return False for a specId already removed",
                SpecManager.removeSpecFiles(specId));
    }


}
