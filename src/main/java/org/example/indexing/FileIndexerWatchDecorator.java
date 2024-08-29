package org.example.indexing;

import org.example.watcher.FileChangeObserverWrapper;

import java.io.File;
import java.util.Set;

public class FileIndexerWatchDecorator implements FileIndexer, AutoCloseable {
    private final FileChangeObserverWrapper fileChangeObserverWrapper;

    private final FileIndexer indexer;

    FileIndexerWatchDecorator(FileChangeObserverWrapper fileChangeObserverWrapper, FileIndexer indexer) {
        this.fileChangeObserverWrapper = fileChangeObserverWrapper;
        this.indexer = indexer;
    }

    @Override
    public void indexDirectory(File directory) {
        fileChangeObserverWrapper.registerDirectory(directory);
        indexer.indexDirectory(directory);
    }

    @Override
    public void unindexDirectory(File directory) {
        indexer.unindexDirectory(directory);
    }

    @Override
    public void indexFile(File file) {
        fileChangeObserverWrapper.registerFile(file);
        indexer.indexFile(file);
    }

    @Override
    public Set<File> search(String word) {
        return indexer.search(word);
    }

    @Override
    public void unindexFile(File file) {
        indexer.unindexFile(file);
    }

    @Override
    public void reindexFile(File file) {
        indexer.reindexFile(file);
    }

    @Override
    public void close() {
        fileChangeObserverWrapper.close();
    }
}
