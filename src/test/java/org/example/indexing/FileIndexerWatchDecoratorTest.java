package org.example.indexing;

import org.example.tokenizer.Tokenizer;
import org.example.tokenizer.WhitespaceTokenizer;
import org.example.watcher.FileChangeObserverWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileIndexerWatchDecoratorTest {
    private FileIndexerWatchDecorator indexer;

    @BeforeEach
    public void setup() {
        Tokenizer tokenizer = new WhitespaceTokenizer();

        FileIndexer fileIndexer = new SimpleFileIndexer(tokenizer);
        FileChangeObserverWrapper fileChangeObserverWrapper = new FileChangeObserverWrapper(fileIndexer);
        indexer = new FileIndexerWatchDecorator(fileChangeObserverWrapper, fileIndexer);
    }

    @AfterEach
    public void teardown() {
        indexer.close();
    }

    @Test
    public void testIndexFile_WhenFileChanged_fileIsReindexed() throws IOException, InterruptedException {
        // create a test file
        File file = Files.createTempFile("test-file", ".txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("before change");
        }

        // index the file
        indexer.indexFile(file);

        Thread.sleep(100);

        // modify the file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("after change");
        }

        // wait for the event to be processed
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertEquals(0, indexer.search("before").size());
            var files = indexer.search("after");
            assertEquals(1, files.size());
            assertTrue(files.contains(file));
        });
    }

    @Test
    public void testIndexFile_WhenFileDeleted_fileIsRemovedFromIndex() throws IOException, InterruptedException {
        // create a test file
        File file = Files.createTempFile("test-file", ".txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("test content");
        }

        // index the file
        indexer.indexFile(file);

        assertEquals(1, indexer.search("test").size());

        Thread.sleep(100);
        // delete the file
        file.delete();

        // wait for the event to be processed
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertEquals(0, indexer.search("test").size(), "deleted file should not be indexed");
        });
    }

    @Test
    public void testIndexFile_WhenDirectoryAlreadyIndexed() throws IOException {
        // create a test directory
        Path testDir = Files.createTempDirectory("test-dir");
        File dir = testDir.toFile();
        // create a file in the directory
        File file = new File(dir, "test-file.txt");
        file.createNewFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("file to delete");
        }

        indexer.indexDirectory(dir);
        indexer.indexFile(file);

        // delete the file
        file.delete();
        // create another file in the directory
        File file2 = new File(dir, "test-file2.txt");
        file2.createNewFile();
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write("file to create");
        }

        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertEquals(0, indexer.search("delete").size(), "deleted file should not be indexed");
            assertEquals(1, indexer.search("create").size(), "created file should be indexed");
        });
    }

}
