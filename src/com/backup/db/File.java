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


@DatabaseTable(tableName = "file")
public class File {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, index = true)
    private String path;
    @DatabaseField(canBeNull = false)
    private String hash;
    @DatabaseField(canBeNull = false)
    private Date createdAt;
    @DatabaseField(canBeNull = false)
    private Date updatedAt;
    @DatabaseField(foreign = true, columnName = "folder")
    private Folder folder;

    public File() {
    }

    public File(String path, String hash, Folder f) {
        this.path = path;
        this.hash = hash;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());
        this.folder = f;
    }

    public File(File copy) {
        this.path = copy.getPath();
        this.hash = copy.getHash();
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());
        this.folder = copy.getFolder();
    }

    public void setHash(String hash) {
        this.updatedAt = new Date(System.currentTimeMillis());
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public int getId() {
        return this.id;
    }

    public String getPath() {
        return this.path;
    }

    public Folder getFolder() {
        return this.folder;
    }

    public static Dao<File,Integer> getDao(ConnectionSource conn) throws SQLException {
        Dao<File, Integer> dao = DaoManager.createDao(conn, File.class);
        TableUtils.createTableIfNotExists(conn, File.class);
        return dao;
    }

    public static File getByPath(ConnectionSource conn, String path) throws SQLException {
        Dao<File, Integer> dao = File.getDao(conn);
        QueryBuilder<File, Integer> builder = dao.queryBuilder();
        builder.where().eq("path", path);
        return dao.queryForFirst(builder.prepare());
    }

    public void updateHash(ConnectionSource conn) throws SQLException {
        Dao<File, Integer> dao = File.getDao(conn);
        File f = File.getByPath(conn, this.getPath());
        if (f == null) {
            dao.create(this);
        } else if(!f.getHash().equals(this.getHash())) {
            f.setHash(this.getHash());
            dao.update(f);
        }
    }
}
