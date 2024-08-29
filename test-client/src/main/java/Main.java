import org.example.indexing.FileIndexer;
import org.example.indexing.FileIndexerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException {
        FileIndexer fileIndexer = FileIndexerFactory.create();

        File testFile = createFile("test.txt", "hello world");
        fileIndexer.indexFile(testFile);

        // Search for words in the indexed files
        System.out.println("Hello is found in: " + fileIndexer.search("hello"));

        // Update the test file and search for words
        for (int i = 0; i < 5; i++) {
            updateFile(testFile, "hello world" + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("world" + i + " is found in: " + fileIndexer.search("world" + i));
        }

        // Create a directory with files and subdirectories and index it
        File directory = createDirectory(10, 10, 5);
        fileIndexer.indexDirectory(directory);

        System.out.println("word1 is found in: " + fileIndexer.search("word1"));

        deleteDirRecursive(directory);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if the directory is unindexed after deletion
        System.out.println("word1 is found in: " + fileIndexer.search("word1"));

    }

    private static File createDirectory(int numFiles, int numSubdirs, int numFilesPerSubdir) throws IOException {
        String resource = Main.class.getResource("/").getFile();
        File directory = new File(resource + "test");
        directory.mkdir();

        // Create subdirectories
        for (int i = 0; i < numSubdirs; i++) {
            File subdir = new File(directory, "subdir" + i);
            subdir.mkdir();
        }

        // Create files in root directory
        for (int i = 0; i < numFiles; i++) {
            createFile(directory, "file" + i + ".txt", "word1 word2");
        }

        // Create files in subdirectories
        for (int i = 0; i < numSubdirs; i++) {
            File subdir = new File(directory, "subdir" + i);
            for (int j = 0; j < numFilesPerSubdir; j++) {
                createFile(subdir, "file" + i + "_" + j + ".txt", "word1 word2");
            }
        }
        return directory;
    }

    private static void createFile(File directory, String filename, String content) throws IOException {
        File file = new File(directory, filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private static File createFile(String filename, String content) throws IOException {
        URL resource = Main.class.getResource("/");
        File file = new File(resource.getFile(), filename);
        file.createNewFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    private static void updateFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private static void deleteDirRecursive(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirRecursive(file);
            }
            else {
                file.delete();
            }
        }
        directory.delete();
    }
}
