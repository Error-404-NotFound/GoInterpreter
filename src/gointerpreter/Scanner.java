package gointerpreter;

import java.lang.String;
import java.lang.Object;
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

    private char advance() {
        current++;
        return source.charAt(current-1);
    }

    private boolean isAtEnd() {
        return current>=source.length();
    }

    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if(current+1>=source.length()) return '\0';
        return source.charAt(current+1);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String token = source.substring(start,current);
        tokens.add(new Token(tokenType,token,literal,line));
    }

    // Polymorphism
    private void addToken(TokenType tokenType) {
        addToken(tokenType,null);
    }
}
