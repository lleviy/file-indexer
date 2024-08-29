package org.example.indexing;

import java.io.File;
import java.util.Set;

public interface FileIndexer {
    void indexDirectory(File directory);

    void unindexDirectory(File directory);

    void indexFile(File file);

    Set<File> search(String word);

    void unindexFile(File file);

    void reindexFile(File file);
}
