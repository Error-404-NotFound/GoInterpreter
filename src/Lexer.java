
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

    public Token getNextToken() {
        while (currentChar != '\0') {
            if (Character.isDigit(currentChar)) {
                int num = Integer.parseInt(String.valueOf(currentChar));
                if(pos+1<text.length() && Character.isDigit(text.charAt(pos+1))) {
                    int iterable_pos = pos;
                    int num_digits = 0;
                    num=0;
                    while(iterable_pos<text.length() && Character.isDigit(text.charAt(iterable_pos))) {
                        iterable_pos++;
                        num_digits++;
                    }
                    for(int i=num_digits-1;i>=0;i--) {
                        num += Integer.parseInt(String.valueOf(text.charAt(pos)))*(int)Math.pow(10, i);
                        pos++;
                    }
                    pos--;
                }
                Token token = new Token(TokenType.INTEGER, String.valueOf(num));
                advance();
                return token;
            } else if (currentChar == '+') {
                Token token = new Token(TokenType.PLUS, "+");
                advance();
                return token;
            } else if (currentChar == '-') {
                Token token = new Token(TokenType.MINUS, "-");
                advance();
                return token;
            } else if (currentChar == '*') {
                Token token = new Token(TokenType.MULTIPLICATION, "*");
                advance();
                return token;
            } else if (currentChar == '/') {
                Token token = new Token(TokenType.DIVISION, "/");
                advance();
                return token;
//            } else if (currentChar == '(') {
//                Token token = new Token(TokenType.L_PAREN, "(");
//                advance();
//                return token;
//            } else if (currentChar == ')') {
//                Token token = new Token(TokenType.R_PAREN, ")");
//                advance();
//                return token;
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
}