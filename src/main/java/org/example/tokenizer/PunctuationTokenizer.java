package org.example.tokenizer;

public class PunctuationTokenizer extends CustomPatternTokenizer {
    public PunctuationTokenizer() {
        super("\\s+|[\\p{Punct}]");
    }
}
