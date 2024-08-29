package org.example.tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface Tokenizer {
    List<String> tokenize(File file);
}
