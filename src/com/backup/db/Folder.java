package com.backup.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "folder")
public class Folder {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, index = true)
    private String path;
    @DatabaseField(canBeNull = false)
    private Date createdAt;
    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    public Folder() {

    }

    public Folder(String path) {
        this.path = path;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());
    }

    public Folder(String path, ConnectionSource conn) throws SQLException {
        this.path = path;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());

        Folder.getDao(conn).create(this);
    }

    public static Dao<Folder,Integer> getDao(ConnectionSource conn) throws SQLException {
        Dao<Folder, Integer> dao = DaoManager.createDao(conn, Folder.class);
        TableUtils.createTableIfNotExists(conn, Folder.class);
        return dao;
    }

    public static Folder getByPath(ConnectionSource conn, String path) throws SQLException {
        Dao<Folder, Integer> dao = Folder.getDao(conn);
        QueryBuilder<Folder, Integer> builder = dao.queryBuilder();
        builder.where().eq("path", path);
        return dao.queryForFirst(builder.prepare());
    }

    public int getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public static boolean exists(String path, ConnectionSource conn) throws SQLException {
        Folder f = Folder.getByPath(conn, path);
        return f != null;
    }

    public void setUpdatedAt() {
        this.updatedAt = new Date(System.currentTimeMillis());
    }

    public static List<Folder> getAll(ConnectionSource conn) throws SQLException {
        return Folder.getDao(conn).queryForAll();
    }

    public static void remove(ConnectionSource conn, String path) throws SQLException {
        Folder folder = Folder.getByPath(conn, path);
        if (folder != null) {
            File.deleteByFolder(conn, folder.getId());
            Folder.getDao(conn).delete(folder);
        }
    }
}
