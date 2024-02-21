package com.github.javaparser.resolution.logic;

import java.io.File;
import java.io.FileWriter;

public class CCHelper {

    private int[] ids;

    private boolean[] called;

    private static final String FileName = "./coverage.txt";

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

    public void printResult() {
        try {
            File file = new File(FileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            String result = "";
            for (int i = 0; i < ids.length; i++) {
                result += String.format("id: %d Covered: %b\n", ids[i], called[i]);
            }
            fw.append(result);
            fw.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }
}
