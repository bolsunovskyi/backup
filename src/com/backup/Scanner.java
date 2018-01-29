package com.backup;

import com.backup.db.Folder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Scanner implements Runnable {
    private Scanned app;
    private Folder folder;
    
    Scanner(Scanned app, Folder folder) {
        this.folder = folder;
        this.app = app;
        this.folder = folder;
    }

    public void run() {
        File startFolder = new File(folder.getPath());
        scanFolder(startFolder);
        app.folderScanned(folder);
    }

    public static String hashFile(String path) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream is = new FileInputStream(path);

            byte[] bytes = new byte[2048];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                md.update(bytes, 0, numBytes);
            }

            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
                String str = Integer.toHexString((b & 0xff));
                if (str.length() == 1) {
                    str = "0"+str;
                }
                sb.append(str);
            }
            is.close();
            return sb.toString();
        } catch (NoSuchAlgorithmException|IOException e) {
            return "";
        }
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
                    String hash = Scanner.hashFile(path);
                    this.app.fileScanned(new com.backup.db.File(path, hash, this.folder));
                } else if(f.isDirectory() && !Files.isSymbolicLink(f.toPath())) {
                    scanFolder(f);
                }
            }
        }
    }
}
