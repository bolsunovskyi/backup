package com.backup;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Scanner implements Runnable {
    private String folder;
    private List<String> files;
    private App app;
    
    Scanner(String folder, App app) {
        this.files = new ArrayList<>();
        this.folder = folder;
        this.app = app;
    }

    public List<String> getFiles() {
        return this.files;
    }

    public void run() {
        File startFolder = new File(folder);
        scanFolder(startFolder);
        app.scanDone(startFolder.getAbsolutePath());
    }
    
    private void scanFolder(File folder) {
        if (!folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    String path = f.getAbsolutePath();
                    //this.files.add(path);
                    System.out.println(path);
                    this.app.scanFile(path);
                } else if(f.isDirectory() && !Files.isSymbolicLink(f.toPath())) {
                    scanFolder(f);
                }
            }
        }
    }
}
