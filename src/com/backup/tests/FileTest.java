package com.backup.tests;

import com.backup.Storage;
import com.backup.db.File;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;

class FileTest {

    @org.junit.jupiter.api.Test
    void getDao() {
        try {
            Storage s = new Storage();
            Dao<File,String> dao = File.getDao(s.getConnection());
            File f = new File("foo", "bar");
            dao.create(f);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}