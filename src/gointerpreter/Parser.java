package gointerpreter;

import java.util.ArrayList;
import java.util.List;
//import static gointerpreter.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Statement declaration() {
        try {
            return null;
        } catch (ParseError error) {
            return null;
        }
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        return tokens.get(current+1);
    }

    private Token peekPrevious() {
        return tokens.get(current-1);
    }

    private boolean isAtEnd() {
        return peek().tokenType == TokenType.EOF;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return tokens.get(current-1);
    }

    private boolean check(TokenType tokenType) {
        if(isAtEnd()) return false;
        return peek().tokenType == tokenType;
    }

    private Token consume(TokenType tokenType, String message) {
        if(check(tokenType)) return advance();
        throw error(peek(), message);
    }

    private boolean match(TokenType... tokenTypes) {
        for(TokenType tokenType: tokenTypes) {
            if(check(tokenType)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private ParseError error(Token token, String message) {
        Main.error(token, message);
        return new ParseError();
    }
}
