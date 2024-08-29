package org.example.indexing;

import org.example.tokenizer.Tokenizer;
import org.example.tokenizer.WhitespaceTokenizer;
import org.example.watcher.FileChangeObserverWrapper;

public class FileIndexerFactory {
    private static volatile FileIndexer instance;

    private FileIndexerFactory() {}

    public static FileIndexer getInstance() {
        return instance;
    }

    public static FileIndexer create(Tokenizer tokenizer) {
        if (instance == null) {
            synchronized (FileIndexerFactory.class) {
                if (instance == null) {
                    FileIndexer fileIndexer = new SimpleFileIndexer(tokenizer);
                    FileChangeObserverWrapper fileChangeObserverWrapper = new FileChangeObserverWrapper(fileIndexer);
                    instance = new FileIndexerWatchDecorator(fileChangeObserverWrapper, fileIndexer);
                    return instance;
                }
            }
        }
        throw new IllegalStateException("Instance already created");
    }

    public static FileIndexer create() {
        return create(new WhitespaceTokenizer());
    }
}

