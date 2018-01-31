package com.backup.tests;

import com.backup.Storage;
import com.backup.db.File;
import com.backup.db.Folder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    private static String dbPath = "test_db.sqlite";
    @Test
    void updateFile() {
        try {
            Storage s = new Storage(dbPath);
            String path = "foo";
            String hash = "bar";
            Folder folder = new Folder("test_f");
            Folder.getDao(s.getConnection()).create(folder);
            new File(path, hash, folder).updateHash(s.getConnection());

            File f1 = File.getByPath(s.getConnection(), path);
            assertEquals(f1.getHash(), hash);

            new File(path, "bar1", folder).updateHash(s.getConnection());

            File f2 = File.getByPath(s.getConnection(), path);
            assertEquals(f2.getId(), f1.getId());
            assertEquals(f2.getPath(), f1.getPath());
            assertEquals(f2.getHash(), "bar1");
            assertEquals(f1.getFolder().getId(), f2.getFolder().getId());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        try {
            Files.delete(Paths.get(dbPath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void getNumbers() {
        try {
            String dbPath = "test_db.sqlite";
            Storage s = new Storage(dbPath);
            Folder folder = new Folder("test_f", s.getConnection());
            File f1 = new File("dqwdwq", "qwdwqd", 10, folder);
            f1.create(s.getConnection());
            File f2 = new File("d11qwdwq", "qw123213sdwqd", 10, folder);
            f2.create(s.getConnection());

            Storage.Numbers n = s.getNumbers();
            assertEquals(2, n.files);
            assertEquals(20, n.filesSize);

            assertEquals(2, n.changed);
            assertEquals(20, n.changedSize);


            f2.setUploadedAt();
            f2.setUpdatedAt(new Date(System.currentTimeMillis() - 100000));
            f2.update(s.getConnection());

            n = s.getNumbers();
            assertEquals(2, n.files);
            assertEquals(20, n.filesSize);

            assertEquals(1, n.changed);
            assertEquals(10, n.changedSize);

            f1.setUploadedAt();
            f2.setUpdatedAt(new Date(System.currentTimeMillis() + 100000));

            n = s.getNumbers();
            assertEquals(2, n.files);
            assertEquals(20, n.filesSize);

            assertEquals(1, n.changed);
            assertEquals(10, n.changedSize);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}