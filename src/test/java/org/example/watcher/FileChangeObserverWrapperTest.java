package org.example.watcher;

import org.example.indexing.FileIndexer;
import org.example.indexing.FileIndexerWatchDecorator;
import org.example.indexing.SimpleFileIndexer;
import org.example.tokenizer.WhitespaceTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class FileChangeObserverWrapperTest {

    private FileIndexer fileIndexer;
    private FileChangeObserverWrapper wrapper;

    Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        fileIndexer = Mockito.mock(FileIndexer.class); // Assuming a basic FileIndexer implementation
        wrapper = new FileChangeObserverWrapper(fileIndexer);

        tempDir = Files.createTempDirectory("test-dir");
    }

    @AfterEach
    void tearDown() {
        wrapper.close();
    }

    @Test
    void testRegisterDirectory() {
        File dir = tempDir.toFile();

        wrapper.registerDirectory(dir);

        assertEquals(1, wrapper.observers.size());
        assertTrue(wrapper.observers.stream().anyMatch(observer -> observer.getDirectory().equals(dir)));
    }

    @Test
    void testRegisterFile() throws IOException {
        File file = Files.createFile(tempDir.resolve("test.txt")).toFile();

        wrapper.registerFile(file);

        assertEquals(1, wrapper.observers.size());
        assertTrue(wrapper.observers.stream().anyMatch(observer -> observer.getDirectory().equals(file.getParentFile())));
    }

    @Test
    void testRegisterFileWithExistingDirectoryObserver() throws IOException {
        File dir = tempDir.toFile();
        wrapper.registerDirectory(dir);
        File file = Files.createFile(dir.toPath().resolve("test.txt")).toFile();

        wrapper.registerFile(file);

        assertEquals(1, wrapper.observers.size()); // No new observer created
    }

    @Test
    void testRemoveObserverWhenDirectoryIsDeleted() throws IOException {
        File dir = tempDir.toFile();
        wrapper.registerDirectory(dir);

        Files.delete(dir.toPath());

        await().atMost(1, TimeUnit.SECONDS).until(() -> wrapper.observers.isEmpty());
    }
}
