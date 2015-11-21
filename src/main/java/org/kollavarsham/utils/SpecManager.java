package org.kollavarsham.utils;

import java.io.File;

/**
 * Utilities to Manage the Specification Files
 */
public class SpecManager {

    private static final String BASE_PATH = "./data/curriculumcourse/";
    private static final String SOLVED = "/solved/";
    private static final String UNSOLVED = "/unsolved/";


    public static String getSolutionFileBasePath(String specId){
        return BASE_PATH + specId + SOLVED;
    }

    public static String getSpecFileBasePath(String specId){
        return BASE_PATH + specId + UNSOLVED;
    }

    public static String getSolutionFilePath(String specId){
        // Test whether specID is valid by checking whether there is a valid unsolved XML
        // in the path before serving the solution file path
        if (getFileNameInFolder(BASE_PATH + specId + UNSOLVED) == ""){
            return "";
        }else{
            return BASE_PATH + specId + SOLVED + "solution.xml";
        }

    }

    public static String getSpecFilePath(String specId){
        String path = BASE_PATH + specId + UNSOLVED;
        return getFileNameInFolder(path);
    }

    public static boolean doesSpecFileExist(String specId){
        String path = BASE_PATH + specId + UNSOLVED;
        if (getFileNameInFolder(path) == ""){
            return false;
        }else{
            return true;
        }
    }

    public static boolean removeSpecFiles(String specId){
        String path = BASE_PATH + specId;
        File specFolder = new File(path);
        return deleteFolder(specFolder);
    }

    private static boolean deleteFolder(File folder) {
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteFolder(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(folder.delete());
    }

    private static String getFileNameInFolder(String path){
        File folder = new File(path);
        String fileName = "";
        if (folder.exists()){
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                    fileName = file.getName();
                }
            }
            if (fileName != ""){
                return path + fileName;

            }

        }
        return fileName;
    }
}
