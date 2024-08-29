package org.example.tokenizer;

public class WhitespaceTokenizer extends CustomPatternTokenizer {
    public WhitespaceTokenizer() {
        super("\\s+");
    }
}
