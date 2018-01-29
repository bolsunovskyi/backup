package com.backup.tests;

import com.backup.Storage;
import com.backup.db.File;
import com.backup.db.Folder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    @Test
    void updateFile() {
        try {
            String dbPath = "test_db.sqlite";
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
            Files.delete(Paths.get(dbPath));
        } catch (SQLException|IOException e) {
            System.out.println(e.getMessage());
        }
    }
}