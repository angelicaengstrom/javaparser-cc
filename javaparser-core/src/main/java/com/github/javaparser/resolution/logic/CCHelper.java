package com.github.javaparser.resolution.logic;

import java.io.File;
import java.io.FileWriter;

public class CCHelper {

    private int[] ids;

    private boolean[] called;

    private static final String FileName = "_coverage.txt";

    public CCHelper(int[] points) {
        ids = points;
        called = new boolean[ids.length];
    }

    public void call(int id) {
        for (int i = 0; i < ids.length; i++) {
            if (id == ids[i]) {
                called[i] = true;
                return;
            }
        }
    }

    public String getCoveragePercentage() {
        int total = ids.length;
        int covered = 0;
        for (int i = 0; i < ids.length; i++) {
            if (called[i]) {
                covered++;
            }
        }
        return String.format("Total: %d Covered: %d Percentage: %.2f \n", total, covered, (double) covered / total * 100);
    }

    public void printResult(String filePrefix) {
        try {
            String fileName = "./" + filePrefix + FileName;
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            String result = "";
            for (int i = 0; i < ids.length; i++) {
                result += String.format("id: %d Covered: %b\n", ids[i], called[i]);
            }
            fw.append(result);
            fw.append("\n-----------------------------------\n");
            fw.append(getCoveragePercentage());
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }
}
