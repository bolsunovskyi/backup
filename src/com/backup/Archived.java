package com.backup;

import java.io.File;

interface Archived {
    void completed(File archive);
    void fileArchived(long progress, long total);
    void fileNotExists(com.backup.db.File file);
}
