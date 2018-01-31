package com.backup.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
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
    @DatabaseField(canBeNull = false, columnName = "created_at")
    private Date createdAt;
    @DatabaseField(canBeNull = false, columnName = "updated_at")
    private Date updatedAt;
    @DatabaseField(columnName = "uploaded_at")
    private Date uploadedAt;
    @DatabaseField(canBeNull = false)
    private long size;
    @DatabaseField(foreign = true, columnName = "folder_id", foreignColumnName = "id")
    private Folder folder;

    public File() {
    }

    public File(String path, String hash, Folder f) {
        this.path = path;
        this.hash = hash;
        this.size = 0;
        this.createdAt = new Date(System.currentTimeMillis());
        this.updatedAt = new Date(System.currentTimeMillis());
        this.folder = f;
    }

    public File(String path, String hash, long size, Folder f) {
        this.path = path;
        this.hash = hash;
        this.size = size;
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

    public static void deleteByFolder(ConnectionSource conn, int folderId) throws SQLException {
        Dao<File, Integer> dao = File.getDao(conn);
        DeleteBuilder<File, Integer> builder = dao.deleteBuilder();
        builder.where().eq("folder_id", folderId);
        dao.delete(builder.prepare());
    }

    public boolean updateHash(ConnectionSource conn) throws SQLException {
        Dao<File, Integer> dao = File.getDao(conn);
        File f = File.getByPath(conn, this.getPath());
        if (f == null) {
            dao.create(this);
            return true;
        } else if(!f.getHash().equals(this.getHash())) {
            f.setHash(this.getHash());
            dao.update(f);
            return true;
        }

        return false;
    }

    public void setUploadedAt() {
        this.uploadedAt = new Date(System.currentTimeMillis());
    }

    public void setUpdatedAt(Date dt) {
        this.updatedAt = dt;
    }

    public void create(ConnectionSource conn) throws SQLException {
        File.getDao(conn).create(this);
    }

    public void update(ConnectionSource conn) throws SQLException {
        File.getDao(conn).update(this);
    }
}
