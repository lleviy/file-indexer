package org.example.exception;

public class IndexingException extends RuntimeException {
    public IndexingException(String message, Throwable e) {
        super(message, e);
    }
}
