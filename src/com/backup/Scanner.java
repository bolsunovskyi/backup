package com.backup;

import com.backup.db.Folder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Scanner implements Runnable {
    private Scanned app;
    private List<Folder> folders;
    
    Scanner(Scanned app) {
        this.app = app;
        this.folders = new ArrayList<>();
    }

    public void addFolder(Folder folder) {
        new Thread(new AddFolder(this.folders, folder)).start();
    }

    public void addFolders(List<Folder> _folders) {
        synchronized (this.folders) {
            this.folders.addAll(_folders);
            this.folders.notify();
        }
    }

    public void run() {
        synchronized (this.folders) {
            while (true) {
                while (folders.size() > 0) {
                    Folder folder = folders.get(0);
                    app.folderStarted(folder);
                    File startFolder = new File(folder.getPath());
                    scanFolder(startFolder, folder);
                    app.folderScanned(folder);
                    folders.remove(0);
                }

                try {
                    folders.wait();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
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
    
    private void scanFolder(File folder, Folder initialFolder) {
        if (!folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    String path = f.getAbsolutePath();
                    String hash = Scanner.hashFile(path);
                    this.app.fileScanned(new com.backup.db.File(path, hash, initialFolder));
                } else if(f.isDirectory() && !Files.isSymbolicLink(f.toPath())) {
                    scanFolder(f, initialFolder);
                }
            }
        }
    }

    private class AddFolder implements Runnable {
        private List<Folder> folders;
        private Folder folder;
        public AddFolder(List<Folder> folders, Folder folder) {
            this.folders = folders;
            this.folder = folder;
        }

        public void run() {
            synchronized (this.folders) {
                this.folders.add(folder);
                this.folders.notify();
            }
        }
    }
}
