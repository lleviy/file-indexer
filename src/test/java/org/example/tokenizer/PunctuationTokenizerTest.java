package org.example.tokenizer;

import org.example.exception.IndexingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PunctuationTokenizerTest {

    @Test
    public void testTokenizer() throws IOException {
        Tokenizer tokenizer = new PunctuationTokenizer();
        File file = File.createTempFile("test", ".txt");
        file.deleteOnExit();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Hello, world! How's it going?");
        }
        List<String> tokens = tokenizer.tokenize(file);
        List<String> expectedTokens = Arrays.asList("Hello", "world", "How", "s", "it", "going");
        assertEquals(expectedTokens, tokens);
    }

    @Test
    public void testTokenizerWithMultiplePunctuations() throws IOException {
        Tokenizer tokenizer = new PunctuationTokenizer();
        File file = File.createTempFile("test", ".txt");
        file.deleteOnExit();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Hello... world!! How's it going?");
        }
        List<String> tokens = tokenizer.tokenize(file);
        List<String> expectedTokens = Arrays.asList("Hello", "world", "How", "s", "it", "going");
        assertEquals(expectedTokens, tokens);
    }

    @Test
    public void testTokenizerWithEmptyFile() throws IOException {
        Tokenizer tokenizer = new PunctuationTokenizer();
        File file = File.createTempFile("test", ".txt");
        file.deleteOnExit();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("");
        }
        List<String> tokens = tokenizer.tokenize(file);
        List<String> expectedTokens = Arrays.asList();
        assertEquals(expectedTokens, tokens);
    }

}
