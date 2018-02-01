package com.backup;

import com.backup.db.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archive {
    private List<File> files;
    private String archiveFolder = "archives";
    public Archive(List<File> files) throws IOException {
        if (!Files.exists(Paths.get(this.archiveFolder))) {
            Files.createDirectory(Paths.get(this.archiveFolder));
        }

        this.files = files;
    }

    public java.io.File create() throws IOException {
        String tm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String zipPath = String.format("%s/archive-%s.zip", this.archiveFolder, tm);
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (File f: this.files) {
            java.io.File fileToZip = new java.io.File(f.getPath());
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(f.getPath());
            zos.putNextEntry(zipEntry);
            int length;
            final byte[] bytes = new byte[1024];
            while((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            fis.close();
        }

        zos.close();
        fos.close();

        return new java.io.File(zipPath);
    }
}
