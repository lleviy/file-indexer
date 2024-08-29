package org.example.tokenizer;

import org.example.exception.IndexingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomPatternTokenizer implements Tokenizer {

    private final String pattern;

    public CustomPatternTokenizer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public List<String> tokenize(File file) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(pattern);
                Collections.addAll(words, tokens);
            }
        } catch (IOException e) {
            throw new IndexingException("Failed to read a file: " + e.getMessage(), e);
        }
        words.removeIf(String::isEmpty);
        return words;
    }
}
