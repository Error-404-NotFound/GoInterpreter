package gointerpreter;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    private boolean isDigit(char c) {
        return c>='0' && c<='9';
    }

    private boolean isAlphabet(char c) {
        return (c>='A' && c<='Z') || (c>='a' && c<='z') || (c=='_');
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlphabet(c);
    }
}
