package com.backup;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

public class Storage {
    ConnectionSource conn;

    public Storage() throws SQLException {
        String url = "jdbc:sqlite:database.sqlite";
        conn = new JdbcConnectionSource(url);
    }

    public ConnectionSource getConnection() {
        return conn;
    }
}
