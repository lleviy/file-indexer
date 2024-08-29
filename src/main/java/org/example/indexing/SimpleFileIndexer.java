package org.example.indexing;

import org.example.tokenizer.Tokenizer;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleFileIndexer implements FileIndexer {

    private final Tokenizer tokenizer;
    private final Map<String, Set<File>> index = new ConcurrentHashMap<>();

    SimpleFileIndexer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void indexDirectory(File directory) {
        if (directory.isDirectory()) {
            Arrays.stream(directory.listFiles()).forEach(file -> {
                if (file.isDirectory()) {
                    indexDirectory(file);
                } else {
                    indexFile(file);
                }
            });
        }
    }

    @Override
    public void unindexDirectory(File directory) {
        System.out.println("Unindexing directory: " + directory);
        for (Map.Entry<String, Set<File>> entry : index.entrySet()) {
            Set<File> files = entry.getValue();
            files.removeIf(file -> file.getAbsolutePath().startsWith(directory.getAbsolutePath()));
            if (files.isEmpty()) {
                index.remove(entry.getKey());
            }
        }
    }

    public void indexFile(File file) {
        if (!isFileIndexed(file)) {
            if (file.isFile()) {
                System.out.println("Indexing " + file);
                List<String> words = tokenizer.tokenize(file);
                for (String word : words) {
                    index.computeIfAbsent(word, k -> ConcurrentHashMap.newKeySet()).add(file);
                }
            }
        }
    }

    private boolean isFileIndexed(File file) {
        for (Collection<File> files : index.values()) {
            if (files.contains(file)) {
                return true;
            }
        }
        return false;
    }

    public void reindexFile(File file) {
        if (file.isFile()) {
            unindexFile(file);
            indexFile(file);
        }
    }

    public void unindexFile(File file) {
        System.out.println("Unindexing " + file);
        for (Map.Entry<String, Set<File>> entry : index.entrySet()) {
            entry.getValue().remove(file);
            if (entry.getValue().isEmpty()) {
                index.remove(entry.getKey());
            }
        }
    }

    public Set<File> search(String word) {
        return index.getOrDefault(word, Collections.emptySet());
    }

}
