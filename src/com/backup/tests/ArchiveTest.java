package com.backup.tests;

import com.backup.Archive;
import com.backup.db.File;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchiveTest {

    @Test
    void create() {
        List<File> files = new ArrayList<>();
        files.add(new File("fixture/zip/test.txt", "sadsad", null));
        files.add(new File("fixture/zip/test2.txt", "saxzczxcdsad", null));
        try {
            Archive a = new Archive(files);
            java.io.File zipFile = a.create();
            assertNotEquals(false, zipFile.exists());
            assertNotEquals(0, zipFile.length());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}