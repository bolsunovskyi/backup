package com.backup.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.Date;


@DatabaseTable(tableName = "file")
public class File {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private String path;
    @DatabaseField(canBeNull = false)
    private String hash;
    @DatabaseField(canBeNull = false)
    private Date createdAt;
    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    public File() {
    }

    public File(String path, String hash) {
        this.path = path;
        this.hash = hash;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());
    }

    public static Dao<File,String> getDao(ConnectionSource conn) throws SQLException {
        Dao<File, String> dao = DaoManager.createDao(conn, File.class);
        TableUtils.createTableIfNotExists(conn, File.class);
        return dao;
    }
}
