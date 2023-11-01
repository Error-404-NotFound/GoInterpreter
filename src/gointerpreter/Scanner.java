package gointerpreter;

import java.lang.String;
import java.lang.Object;
import java.lang.Double;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int pos_index = 0;
    private int line = 1;

    //Constructor for Scanner Class
    public Scanner(String source) {
        this.source = source;
    }

    //////////////////////////////////////////////////////////
    //      Creating Literals
    //////////////////////////////////////////////////////////


    //Number Literal
    private void number() {
        while(isDigit(peek())) advance();
        //for checking floating point number
        if(peek()=='.' && isDigit(peekNext())) {
            advance();
            while(isDigit(peek())) advance();
        }
        addToken(TokenType.NUMBER,Double.parseDouble(source.substring(start,pos_index)));
    }

    //String Literal
    private void string() {

    }

    //Identifier Literal
    private void identifier() {

    }

    //////////////////////////////////////////////////////////
    //      End of Creating Literals
    //////////////////////////////////////////////////////////


    //Different Methods for different purposes
    private boolean isDigit(char c) {
        return c>='0' && c<='9';
    }

    private boolean isAlphabet(char c) {
        return (c>='A' && c<='Z') || (c>='a' && c<='z') || (c=='_');
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlphabet(c);
    }

    //gives the char at the current position and increases the position index
    private char advance() {
        pos_index++;
        return source.charAt(pos_index-1);
    }

    //checks if pos_index is at the end
    private boolean isAtEnd() {
        return pos_index>=source.length();
    }

    //gives the character at pos_index
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(pos_index);
    }

    //gives the charcter at pos_index+1
    private char peekNext() {
        if(pos_index+1>=source.length()) return '\0';
        return source.charAt(pos_index+1);
    }

    // Make a token and make it an object and add it to the tokens list and add literal associated to it
    private void addToken(TokenType tokenType, Object literal) {
        String token_text = source.substring(start,pos_index);
        tokens.add(new Token(tokenType,token_text,literal,line));
    }

    // Polymorphism
    // Used to add Token
    private void addToken(TokenType tokenType) {
        addToken(tokenType,null);
    }
}
