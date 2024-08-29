package org.example.watcher;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.example.indexing.FileIndexer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileChangeObserverWrapper implements AutoCloseable{

    final Set<FileAlterationObserver> observers = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(6);
    private final FileIndexer fileIndexer;

    public FileChangeObserverWrapper(FileIndexer fileIndexer) {
        this.fileIndexer = fileIndexer;
        pollEvents();
    }

    public void registerDirectory(File dir) {
        FileAlterationObserver observer = new FileAlterationObserver(dir);
        observer.addListener(new FileChangeListener(fileIndexer));
        observers.add(observer);

        // Remove existing observers for specific files in the directory
        List<FileAlterationObserver> observersToRemove = new ArrayList<>();
        for (FileAlterationObserver existingObserver : observers) {
            if (existingObserver.getDirectory().equals(dir)) {
                continue; // Skip the directory observer itself
            }
            if (dir.equals(existingObserver.getDirectory().getParentFile())) {
                observersToRemove.add(existingObserver);
            }
        }
        observers.removeAll(observersToRemove);
    }

    public void registerFile(File file) {
        File parentDir = file.getParentFile();
        // Check if an observer for the parent directory already exists
        for (FileAlterationObserver observer : observers) {
            if (observer.getDirectory().equals(parentDir)) {
                // If an observer exists, don't create a new one
                return;
            }
        }
        // No observer found, create a new one
        FileAlterationObserver observer = new FileAlterationObserver(parentDir, f -> f.getName().equals(file.getName()));
        observer.addListener(new FileChangeListener(fileIndexer));
        observers.add(observer);
    }

    private void pollEvents() {
        scheduler.scheduleAtFixedRate(() -> {
            for (FileAlterationObserver observer : observers) {
                File directory = observer.getDirectory();
                if (!directory.exists()) {
                    for (FileAlterationListener listener : observer.getListeners()) {
                        listener.onStop(observer);
                    }
                    observers.remove(observer); // remove observer if directory no longer exists
                } else {
                    observer.checkAndNotify();
                }
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}
