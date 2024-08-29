package org.example.watcher;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.example.indexing.FileIndexer;

import java.io.File;

public class FileChangeListener extends FileAlterationListenerAdaptor {

    private final FileIndexer fileIndexer;

    public FileChangeListener(FileIndexer fileIndexer) {
        this.fileIndexer = fileIndexer;
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate event: " + file);
        fileIndexer.indexFile(file);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete event: " + file);
        fileIndexer.unindexFile(file);
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange event: " + file);
        fileIndexer.reindexFile(file);
    }

    public void onStop(FileAlterationObserver observer) {
        File dir = observer.getDirectory();
        if (!dir.exists()) {
            fileIndexer.unindexDirectory(dir);
        };
    }
}
