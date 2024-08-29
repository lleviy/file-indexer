package org.example.tokenizer;

import org.example.exception.IndexingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomPatternTokenizerTest {

    @Test
    public void testTokenize() throws IOException {
        File file = File.createTempFile("test", ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("hello world\n");
            writer.write("this is a test\n");
        }
        CustomPatternTokenizer tokenizer = new CustomPatternTokenizer("\\s+");

        List<String> tokens = tokenizer.tokenize(file);

        assertEquals(6, tokens.size());
        assertEquals("hello", tokens.get(0));
        assertEquals("world", tokens.get(1));
        assertEquals("this", tokens.get(2));
        assertEquals("is", tokens.get(3));
        assertEquals("a", tokens.get(4));
        assertEquals("test", tokens.get(5));
    }

    @Test
    public void testTokenizeEmptyFile() throws IOException {
        File file = File.createTempFile("test", ".txt");
        CustomPatternTokenizer tokenizer = new CustomPatternTokenizer("\\s+");

        List<String> tokens = tokenizer.tokenize(file);

        assertTrue(tokens.isEmpty());
    }

    @Test
    public void testTokenizeInvalidPattern() {
        CustomPatternTokenizer tokenizer = new CustomPatternTokenizer("[");

        assertThrows(IndexingException.class, () -> tokenizer.tokenize(new File("test.txt")));
    }

    @Test
    public void testTokenizerWithNonExistentFile() {
        Tokenizer tokenizer = new CustomPatternTokenizer("\\s+");
        File file = new File("non-existent-file.txt");

        assertThrows(IndexingException.class, ()  -> tokenizer.tokenize(file));
    }

}
