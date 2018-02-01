package com.backup.tests;

import com.backup.Storage;
import com.backup.db.File;
import com.backup.db.Folder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

class FileTest {

    @org.junit.jupiter.api.Test
    void getDao() {
        try {
            Storage s = new Storage("fixture/_test_db_test.sqlite");
            Folder folder = new Folder("test");
            Folder.getDao(s.getConnection()).create(folder);

            File f = new File("foo", "bar", folder);
            File.getDao(s.getConnection()).create(f);

            Files.delete(Paths.get("fixture/_test_db_test.sqlite"));
        } catch (SQLException|IOException e) {
            System.out.println(e.getMessage());
        }
    }
}