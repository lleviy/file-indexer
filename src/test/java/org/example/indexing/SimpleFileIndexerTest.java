package org.example.indexing;

import org.example.tokenizer.WhitespaceTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleFileIndexerTest {

    File directory;
    File file1;
    File file2;

    FileIndexer service;

    @BeforeEach
    public void setUp() throws IOException {
        service = new SimpleFileIndexer(new WhitespaceTokenizer());

        directory = new File("test");
        directory.mkdir();
        file1 = new File(directory, "file1.txt");
        file2 = new File(directory, "file2.txt");

        // filling files
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write("word1 word2");
        }

        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("word1");
        }
    }

    @AfterEach
    public void tearDown() {
        deleteDirRecursive(directory);
    }

    private void deleteDirRecursive(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirRecursive(file);
            }
            else file.delete();
        }
        directory.delete();
    }

    @Test
    public void testSearch_WhenNoFileIndexed() {
        Set<File> files = service.search("word1");
        assertEquals(0, files.size());
    }

    @Test
    public void testSearch_WhenDirectoryIndexed() {
        service.indexDirectory(directory);

        var files = service.search("word1");
        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test
    public void testSearch_WhenDirectoryWithSubdirectoriesIndexed() throws IOException {
        // Create subdirectories
        File subdir1 = new File(directory, "subdir1");
        subdir1.mkdir();
        File subdir2 = new File(directory, "subdir2");
        subdir2.mkdir();

        // Create files in each subdirectory
        File fileSubdir = new File(subdir1, "file.txt");
        fileSubdir.createNewFile();
        try (FileWriter writer = new FileWriter(fileSubdir)) {
            writer.write("word1 word2");
        }

        File fileSubdir2 = new File(subdir1, "file2.txt");
        fileSubdir2.createNewFile();
        try (FileWriter writer = new FileWriter(fileSubdir2)) {
            writer.write("word2 word3");
        }

        File fileSubdir3 = new File(subdir2, "file.txt");
        fileSubdir3.createNewFile();
        try (FileWriter writer = new FileWriter(fileSubdir3)) {
            writer.write("word1 word2");
        }

        service.indexDirectory(directory);

        var files = service.search("word1");
        assertEquals(4, files.size());
        assertTrue(files.containsAll(List.of(file1, file2, fileSubdir, fileSubdir3)));
    }

    @Test
    public void testSearch_WhenFilesIndexed() {
        service.indexFile(file1);
        service.indexFile(file2);

        var files = service.search("word1");
        assertEquals(2, files.size());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test
    public void testSearch_WhenFileIndexedTwice() {
        service.indexFile(file1);
        service.indexFile(file1);

        var files = service.search("word1");
        assertEquals(1, files.size());
    }

    @Test
    public void testIndexDirectory() throws IOException {
        int numFiles = 100; // number of files to generate
        int numSubdirs = 50; // number of subdirectories to generate
        int numFilesPerSubdir = 20; // number of files per subdirectory

        createDirectory(numFiles, numSubdirs, numFilesPerSubdir);

        service.indexDirectory(directory);
        service.indexDirectory(directory);

        var files = service.search("word1");
        assertEquals(numFiles + numSubdirs * numFilesPerSubdir, files.size());
    }

    private void createDirectory(int numFiles, int numSubdirs, int numFilesPerSubdir) throws IOException {
        directory = new File("test");
        directory.mkdir();

        // create subdirectories
        for (int i = 0; i < numSubdirs; i++) {
            File subdir = new File(directory, "subdir" + i);
            subdir.mkdir();
        }

        // create files in root directory
        for (int i = 0; i < numFiles; i++) {
            File file = new File(directory, "file" + i + ".txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("word1 word2"); // write some content to the file
            }
        }

        // create files in subdirectories
        for (int i = 0; i < numSubdirs; i++) {
            File subdir = new File(directory, "subdir" + i);
            for (int j = 0; j < numFilesPerSubdir; j++) {
                File file = new File(subdir, "file" + i + "_" + j + ".txt");
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("word1 word2"); // write some content to the file
                }
            }
        }
    }

//    private File createFile(String filename, String content) throws IOException {
//        File file = new File(directory, filename);
//        try (FileWriter writer = new FileWriter(file)) {
//            writer.write(content);
//        }
//        return file;
//    }
//
//    private void createDirectory(int numFiles, int numSubdirs, int numFilesPerSubdir) throws IOException {
//        // create subdirectories
//        for (int i = 0; i < numSubdirs; i++) {
//            File subdir = new File(directory, "subdir" + i);
//            subdir.mkdir();
//        }
//
//        // create files in root directory
//        for (int i = 0; i < numFiles; i++) {
//            createFile("file" + i + ".txt", "word1 word2");
//        }
//
//        // create files in subdirectories
//        for (int i = 0; i < numSubdirs; i++) {
//            File subdir = new File(directory, "subdir" + i);
//            for (int j = 0; j < numFilesPerSubdir; j++) {
//                File file = new File(subdir, "file" + i + "_" + j + ".txt");
//                createFile(file, "word1 word2");
//            }
//        }
//    }
}
