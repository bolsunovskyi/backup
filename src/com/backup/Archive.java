package com.backup;

import com.backup.db.File;
import com.j256.ormlite.support.ConnectionSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archive implements Runnable {
    private List<File> files;
    private String archiveFolder = "archives";
    private Archived app;
    private long progress;
    private long total;
    private ConnectionSource conn;

    public Archive(List<File> files) throws IOException {
        if (!Files.exists(Paths.get(this.archiveFolder))) {
            Files.createDirectory(Paths.get(this.archiveFolder));
        }

        this.total = files.size();
        this.files = files;
    }

    public Archive(ConnectionSource conn, Archived app) throws IOException,SQLException {
        this.conn = conn;

        if (!Files.exists(Paths.get(this.archiveFolder))) {
            Files.createDirectory(Paths.get(this.archiveFolder));
        }

        this.app = app;
    }

    public java.io.File create() throws IOException {
        this.progress = 0;
        String tm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String zipPath = String.format("%s/archive-%s.zip", this.archiveFolder, tm);
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (File f: this.files) {
            if (!Files.exists(Paths.get(f.getPath()))) {
                if (this.app != null) {
                    this.app.fileNotExists(f);
                }
                continue;
            }

            try {
                java.io.File fileToZip = new java.io.File(f.getPath());
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(f.getPath());
                zos.putNextEntry(zipEntry);
                int length;
                final byte[] bytes = new byte[1024];
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                fis.close();
                this.progress++;
                if (this.app != null) {
                    this.app.fileArchived(this.progress, this.total);
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

        zos.close();
        fos.close();

        java.io.File r = new java.io.File(zipPath);
        if (this.app != null) {
            this.app.completed(r);
        }

        return r;
    }

    public void run() {
        try {
            if (this.files == null && this.conn != null) {
                this.files = File.updatedFiles(this.conn);
                this.total = files.size();
            }

            this.create();
        } catch (IOException|SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
