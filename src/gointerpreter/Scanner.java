package gointerpreter;

import java.lang.String;
import java.lang.Object;
import java.lang.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static gointerpreter.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int pos_index = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("fmt.Println",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
        keywords.put("package",PACKAGE);
        keywords.put("import", IMPORT);
        keywords.put("main",   MAIN);
        keywords.put("func",   FUNC);
        keywords.put("fmt",    FMT);
    }

    //Constructor for Scanner Class
    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start=pos_index;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF,"",null,line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(':addToken(TokenType.LEFT_PAREN);break;
            case ')':addToken(TokenType.RIGHT_PAREN);break;
            case '{':addToken(TokenType.LEFT_BRACE);break;
            case '}':addToken(TokenType.RIGHT_BRACE);break;
            case ',':addToken(TokenType.COMMA);break;
            case '.':addToken(TokenType.DOT);break;
            case '-':addToken(TokenType.MINUS);break;
            case '+':addToken(TokenType.PLUS);break;
            case ';':addToken(TokenType.SEMICOLON);break;
            case '*':addToken(TokenType.STAR);break;
            case '!':addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=':addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '<':addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '>':addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    while (peek() != '*' && peekNext() != '/' && !isAtEnd()) {
                        if (peek() == '\n') line++;
                        advance();
                    }
                    if (isAtEnd()) {
                        System.out.println("Error: Unterminated Comment");
                        return;
                    }
                    advance();
                    advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':line++;break;
            case '"':string();break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlphabet(c)) {
                    identifier();
                } else {
                    System.out.println("Error: Unexpected Character");
                }
        }
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
        while(peek()!='"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()) {
            System.out.println("Error: Unterminated String");
            return;
        }

        // to close the string by a quote
        advance();

        // removing the quotes from the string
        addToken(TokenType.STRING,source.substring(start+1,pos_index-1));
    }

    //Identifier Literal
    private void identifier() {
        while(isAlphabet(peek())) advance();
        // to check if the identifier is a keyword
        String text = source.substring(start,pos_index);
        TokenType type = keywords.get(text);
        // if it is not a keyword then it is an identifier
        if(type==null) type=IDENTIFIER;
        addToken(type);
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

    private boolean match(char expected) {
        if(isAtEnd()) return false;
        // checks if the character at pos_index is equal to expected
        if(source.charAt(pos_index)!=expected) return false;
        pos_index++;
        return true;
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
