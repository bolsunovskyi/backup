package com.backup;

import com.backup.db.File;
import com.backup.db.Folder;

interface Scanned {
    void fileScanned(File file, long progress, long total);
    void fileIndexed(Folder folder, long n);
    void folderScanned(Folder folder);
    void folderStarted(Folder folder);
    void queueFinished();
}
