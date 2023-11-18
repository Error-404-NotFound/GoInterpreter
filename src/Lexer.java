
// Lexer class to generate tokens from input
class Lexer {
    private String text;
    private int pos;
    private char currentChar;

    public Lexer(String text) {
        this.text = text;
        this.pos = 0;
        this.currentChar = text.charAt(pos);
    }

    public void advance() {
        pos++;
        if (pos < text.length()) {
            currentChar = text.charAt(pos);
        } else {
            currentChar = '\0'; // '\0' represents end of input
        }
    }

    public int getPos() {
        return pos;
    }

    public Token setPos(int newPos) {
        if (newPos >= 0 && newPos < text.length()) {
            pos = newPos;
            currentChar = text.charAt(pos);
            Token token = getNextToken();
            return token;
        } else {
            throw new RuntimeException("Invalid position");
        }
    }

    public Token getNextToken() {
        while (currentChar != '\0') {
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return identifier();
            } else if (Character.isDigit(currentChar)) {
                return integer();
            } else if (currentChar == '+') {
                advance();
                return new Token(TokenType.PLUS, "+");
            } else if (currentChar == '-') {
                advance();
                return new Token(TokenType.MINUS, "-");
            } else if (currentChar == '*') {
                advance();
                return new Token(TokenType.MUL, "*");
            } else if (currentChar == '/') {
                advance();
                return new Token(TokenType.DIV, "/");
            } else if (currentChar == '(') {
                advance();
                return new Token(TokenType.LPAREN, "(");
            } else if (currentChar == ')') {
                advance();
                return new Token(TokenType.RPAREN, ")");
            } else if (currentChar == '{') {
                advance();
                return new Token(TokenType.LBRACE, "{");
            } else if (currentChar == '}') {
                advance();
                return new Token(TokenType.RBRACE, "}");
            } else if (currentChar == '\"') {
                advance();
                return new Token(TokenType.DOUBLE_quote, "\"");
            }  else if (currentChar == '<') {
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LTE, "<=");
                } else {
                    return new Token(TokenType.LT, "<");
                }
            } else if (currentChar == '>') {
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GTE, ">=");
                } else {
                    return new Token(TokenType.GT, ">");
                }
            } else if (currentChar == '=') {
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQ, "==");
                } else {
                    return new Token(TokenType.ASSIGN, "=");
                }
            } else if (currentChar == '!') {
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NEQ, "!=");
                } else {
                    throw new RuntimeException("Invalid character");
                }
            } else if (currentChar == ';') {
                advance();
                return new Token(TokenType.SEMICOLON, ";");
            } else {
                // Ignore whitespace
                if (Character.isWhitespace(currentChar)) {
                    advance();
                    continue;
                }
                throw new RuntimeException("Invalid character");
            }
        }
        return new Token(TokenType.EOF, null);
    }

    private char peek() {
        int peekPos = pos + 1;
        return peekPos < text.length() ? text.charAt(peekPos) : '\0';
    }
    private Token identifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_' || currentChar == '.')) {
            result.append(currentChar);
            advance();
        }
        String identifier = result.toString();
        switch (identifier) {
            case "if":
                return new Token(TokenType.IF, identifier);
            case "else":
                return new Token(TokenType.ELSE, identifier);
            case "elseif":
                return new Token(TokenType.ELSEIF, identifier);
            case "fmt.Println":
                return new Token(TokenType.PRINTLN, identifier);
            case "fmt":
                return new Token(TokenType.FMT, identifier);
            case "for":
                return new Token(TokenType.FOR, identifier);
            case "import":
                return new Token(TokenType.IMPORT, identifier);
            case "main":
                return new Token(TokenType.MAIN, identifier);
            case "package":
                return new Token(TokenType.PACKAGE, identifier);
            case "func":
                return new Token(TokenType.FUNC, identifier);
            default:
                return new Token(TokenType.IDENTIFIER, identifier);
        }

    }

    private Token integer() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && Character.isDigit(currentChar)) {
            result.append(currentChar);
            advance();
        }
        return new Token(TokenType.INT, result.toString());
    }
}