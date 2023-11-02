
// Interpreter class to interpret tokens
class Interpreter {
    private Lexer lexer;
    private Token currentToken;

    public Interpreter(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    public void eat(TokenType tokenType) {
        if (currentToken.type == tokenType) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Invalid syntax");
        }
    }

//    public int factor() {
//        Token token = currentToken;
//        if (token.type == TokenType.INTEGER) {
//            eat(TokenType.INTEGER);
//            return Integer.parseInt(token.value);
//        } else if (token.type == TokenType.L_PAREN) {
//            eat(TokenType.L_PAREN);
//            int result = expr();
//            eat(TokenType.R_PAREN);
//            return result;
//        } else {
//            throw new RuntimeException("Invalid syntax");
//        }
//    }

    public int expr() {
        Token resultToken = new Token(TokenType.INTEGER, String.valueOf(0));
        Token left = currentToken;
        eat(TokenType.INTEGER);
        while(currentToken.type!=TokenType.EOF) {
            Token op = currentToken;
            if (op.type == TokenType.PLUS) {
                eat(TokenType.PLUS);
            } else if(op.type == TokenType.MINUS) {
                eat(TokenType.MINUS);
            } else if(op.type == TokenType.MULTIPLICATION) {
                eat(TokenType.MULTIPLICATION);
            } else if(op.type == TokenType.DIVISION) {
                eat(TokenType.DIVISION);
            }

            Token right = currentToken;
            eat(TokenType.INTEGER);

            if (op.type == TokenType.PLUS) {
                resultToken.value = String.valueOf((Integer.parseInt(left.value) + Integer.parseInt(right.value)));
            } else if(op.type == TokenType.MINUS){
                resultToken.value = String.valueOf((Integer.parseInt(left.value) - Integer.parseInt(right.value)));
            } else if(op.type == TokenType.MULTIPLICATION){
                resultToken.value = String.valueOf((Integer.parseInt(left.value) * Integer.parseInt(right.value)));
            } else {
                resultToken.value = String.valueOf((Integer.parseInt(left.value) / Integer.parseInt(right.value)));
            }
            left = resultToken;
        }
        return Integer.parseInt(resultToken.value);
    }
}