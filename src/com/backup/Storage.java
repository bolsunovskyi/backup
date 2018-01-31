package com.backup;

import com.backup.db.File;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

public class Storage {
    private ConnectionSource conn;

    public Storage() throws SQLException {
        String url = "jdbc:sqlite:database.sqlite";
        conn = new JdbcConnectionSource(url);
    }

    public Storage(String path) throws SQLException {
        String url = "jdbc:sqlite:" + path;
        conn = new JdbcConnectionSource(url);
    }

    public ConnectionSource getConnection() {
        return conn;
    }

    public class Numbers {
        public long files;
        public long filesSize;
        public long changed;
        public long changedSize;

        private String getSizeMb(long in) {
            return String.format("%.2f Mb", ((float)in) / 1048576);
        }

        private String getSizeKb(long in) {
            return String.format("%.2f Kb", ((float)in) / 1024);
        }

        private String getSizeGb(long in) {
            return String.format("%.2f Gb", ((float)in) / 1073741824);
        }

        public String getFilesSizeStr() {
            if (filesSize > 1073741824) {
                return getSizeGb(filesSize);
            }
            if(filesSize > 1048576) {
                return getSizeMb(filesSize);
            }
            if (filesSize > 1024) {
                return getSizeKb(filesSize);
            }

            return String.format("%d bytes", filesSize);
        }

        public String getChangedFilesSizeStr() {
            if (changedSize > 1073741824) {
                return getSizeGb(changedSize);
            }
            if(changedSize > 1048576) {
                return getSizeMb(changedSize);
            }
            if (changedSize > 1024) {
                return getSizeKb(changedSize);
            }

            return String.format("%d bytes", changedSize);
        }

        Numbers() {}
    }

    public Numbers getNumbers() throws SQLException {
        Numbers n = new Numbers();

        Dao<File, Integer> dao = File.getDao(this.conn);
        n.files = dao.countOf();
        n.filesSize = dao.queryRawValue("SELECT SUM(size) FROM file");

        n.changed = dao.queryRawValue("SELECT COUNT(size) FROM file WHERE uploaded_at IS NULL OR updated_at > uploaded_at");
        n.changedSize = dao.queryRawValue("SELECT SUM(size) FROM file WHERE uploaded_at IS NULL OR updated_at > uploaded_at");

        return n;
    }
}
