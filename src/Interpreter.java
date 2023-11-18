import java.util.HashMap;
import java.util.Map;

// Interpreter class to interpret tokens
class Interpreter {
    private Lexer lexer;
    private Token currentToken;
    private Map<String, Symbol> symbolTable;

    String text;

    public Interpreter(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
        this.symbolTable = new HashMap<>();
        eat(TokenType.PACKAGE);
        eat(TokenType.MAIN);
        eat(TokenType.IMPORT);
        eat(TokenType.LPAREN);
        eat(TokenType.DOUBLE_quote);
        eat(TokenType.FMT);
        eat(TokenType.DOUBLE_quote);
        eat(TokenType.RPAREN);
        eat(TokenType.FUNC);
        eat(TokenType.MAIN);
        eat(TokenType.LPAREN);
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);
    }

    private void eat(TokenType tokenType) {
        if (currentToken.type == tokenType) {
            currentToken = lexer.getNextToken();
        } else {
            throw new RuntimeException("Invalid syntax");
        }
    }

    public void interpret() {
        while (currentToken.type != TokenType.EOF) {
            statement();
        }
    }

    private void statement() {
        if (currentToken.type == TokenType.IDENTIFIER) {
            declarationOrAssignment();
        } else if (currentToken.type == TokenType.PRINTLN) {
            printStatement();
        } else if (currentToken.type == TokenType.IF) {
            ifStatement();
        } else if (currentToken.type == TokenType.ELSEIF) {
            elseifStatement();
        } else if (currentToken.type == TokenType.ELSE) {
            elseStatement();
        } else if (currentToken.type == TokenType.FOR) {
            forStatement();
        } else if (currentToken.type == TokenType.RBRACE) {
            eat(TokenType.RBRACE);
        } else {
            throw new RuntimeException("Invalid statement");
        }
    }

    private void declarationOrAssignment() {
        Token variableToken = currentToken;
        eat(TokenType.IDENTIFIER);

        if (currentToken.type == TokenType.ASSIGN) {
            eat(TokenType.ASSIGN);

            float value = expr(); // Change here: evaluate the expression

            Symbol symbol = new Symbol(variableToken.value, value);
            symbolTable.put(variableToken.value, symbol);

            //System.out.println("Variable: " + variableToken.value + " Value: " + value);
        }
        if (currentToken.type == TokenType.SEMICOLON){
            eat(TokenType.SEMICOLON); // Just a declaration without assignment, so consume semicolon
        }
    }

    private void printStatement() {
        eat(TokenType.PRINTLN);
        eat(TokenType.LPAREN);

        if (currentToken.type == TokenType.IDENTIFIER) {
            // fmt.Println(x)
            String variableName = currentToken.value;
            eat(TokenType.IDENTIFIER);

            if (symbolTable.containsKey(variableName)) {
                System.out.println(symbolTable.get(variableName).value);
            } else {
                throw new RuntimeException("Variable '" + variableName + "' not declared");
            }
        } else {
            // fmt.Println(expression)
            float value = expr();
            System.out.println(value);
        }

        eat(TokenType.RPAREN);
        eat(TokenType.SEMICOLON);
    }
    boolean var = false;
    private void ifStatement() {
        eat(TokenType.IF);
        eat(TokenType.LPAREN);
        boolean condition = expr() != 0;
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);


        if (condition) {
            while (currentToken.type != TokenType.ELSE && currentToken.type != TokenType.ELSEIF && currentToken.type != TokenType.RBRACE) {
                statement();
            }
            var = true;
            eat(TokenType.RBRACE);

        } else {
            // Skip statements inside the if block
            while (currentToken.type != TokenType.ELSE && currentToken.type != TokenType.ELSEIF && currentToken.type != TokenType.RBRACE) {
                eat(currentToken.type);
            }
            eat(TokenType.RBRACE);
        }

        boolean condition2 = false;
        while (currentToken.type == TokenType.ELSEIF && !condition && !var) {
            condition2 = elseifStatement();
        }

        if (currentToken.type == TokenType.ELSE && !condition2 && !var) {
            elseStatement();
        }


    }

    private boolean elseifStatement() {
        eat(TokenType.ELSEIF);
        eat(TokenType.LPAREN);
        boolean condition = expr() != 0;
        eat(TokenType.RPAREN);
        eat(TokenType.LBRACE);

        if(!var) {
            if (condition) {
                while (currentToken.type != TokenType.ELSE && currentToken.type != TokenType.ELSEIF && currentToken.type != TokenType.RBRACE) {
                    statement();
                }
                var = true;
                eat(TokenType.RBRACE);
                return condition;
            } else {
                // Skip statements inside the elseif block
                while (currentToken.type != TokenType.ELSE && currentToken.type != TokenType.ELSEIF && currentToken.type != TokenType.RBRACE) {
                    eat(currentToken.type);
                }
                eat(TokenType.RBRACE);
                return false;
            }
        }
        else {
            while (currentToken.type != TokenType.ELSE && currentToken.type != TokenType.ELSEIF && currentToken.type != TokenType.RBRACE) {
                eat(currentToken.type);
            }
            eat(TokenType.RBRACE);
            return false;
        }
    }

    private void elseStatement() {
        eat(TokenType.ELSE);
        eat(TokenType.LBRACE);

        if(!var) {
            while (currentToken.type != TokenType.RBRACE) {
                statement();
            }
        }
        else{
            while(currentToken.type!=TokenType.RBRACE){
                eat(currentToken.type);
            }
        }
        eat(TokenType.RBRACE);
    }

    private void forStatement() {
        eat(TokenType.FOR);
        eat(TokenType.LPAREN);
        // Parse initialization
        declarationOrAssignment();
        // Parse condition
        int conditionPosition = lexer.getPos() - 1 ;
        float condition = expr();
        eat(TokenType.SEMICOLON);
        // Storing the position for coming back again
        int updatePosition = lexer.getPos() - 1 ;
        // Parse update without updating
        while(currentToken.type!=TokenType.RPAREN) {
            eat(currentToken.type);
        }
        //declarationOrAssignment();
        eat(TokenType.RPAREN);
        int loopBodyPosition = lexer.getPos() ;
        eat(TokenType.LBRACE);

        while (condition != 0) {
            // Execute statements inside the loop
            //currentToken = lexer.setPos(loopBodyPosition);
            //System.out.println(currentToken);
            while (currentToken.type != TokenType.RBRACE) {
                statement();
            }
            //RBRACE acheived
            // Parse the update part again
            currentToken =  lexer.setPos(updatePosition);

            //System.out.println(currentToken);
            while(currentToken.type!=TokenType.RPAREN) {
                statement();
            }
            currentToken =  lexer.setPos(conditionPosition);
            condition = expr();
            if (condition != 0) {
                currentToken = lexer.setPos(loopBodyPosition);
            }
            else {
                while(currentToken.type!=TokenType.RBRACE) {
                    eat(currentToken.type);
                }
            }
        }
        eat(TokenType.RBRACE);

    }

    private float expr() {
        float result = term();

        while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS ||
                currentToken.type == TokenType.LT || currentToken.type == TokenType.LTE ||
                currentToken.type == TokenType.GT || currentToken.type == TokenType.GTE ||
                currentToken.type == TokenType.EQ || currentToken.type == TokenType.NEQ) {
            Token operator = currentToken;
            if (operator.type == TokenType.PLUS) {
                eat(TokenType.PLUS);
                result += term();
            } else if (operator.type == TokenType.MINUS) {
                eat(TokenType.MINUS);
                result -= term();
            } else if (operator.type == TokenType.LT) {
                eat(TokenType.LT);
                result = (result < term()) ? 1 : 0;
            } else if (operator.type == TokenType.LTE) {
                eat(TokenType.LTE);
                result = (result <= term()) ? 1 : 0;
            } else if (operator.type == TokenType.GT) {
                eat(TokenType.GT);
                result = (result > term()) ? 1 : 0;
            } else if (operator.type == TokenType.GTE) {
                eat(TokenType.GTE);
                result = (result >= term()) ? 1 : 0;
            } else if (operator.type == TokenType.EQ) {
                eat(TokenType.EQ);
                result = (result == term()) ? 1 : 0;
            } else if (operator.type == TokenType.NEQ) {
                eat(TokenType.NEQ);
                result = (result != term()) ? 1 : 0;
            }
        }

        return result;
    }

    private float term() {
        float result = factor();

        while (currentToken.type == TokenType.MUL || currentToken.type == TokenType.DIV) {
            Token operator = currentToken;
            if (operator.type == TokenType.MUL) {
                eat(TokenType.MUL);
                result *= factor();
            } else if (operator.type == TokenType.DIV) {
                eat(TokenType.DIV);
                float divisor = factor();
                if (divisor != 0) {
                    result /= divisor;
                } else {
                    throw new RuntimeException("Division by zero");
                }
            }
        }

        return result;
    }

    private float factor() {
        if (currentToken.type == TokenType.INT) {
            int value = Integer.parseInt(currentToken.value);
            eat(TokenType.INT);
            return value;
        } else if (currentToken.type == TokenType.IDENTIFIER) {
            String variableName = currentToken.value;
            eat(TokenType.IDENTIFIER);

            if (symbolTable.containsKey(variableName)) {
                return symbolTable.get(variableName).value;
            } else {
                throw new RuntimeException("Variable '" + variableName + "' not declared");
            }
        } else if (currentToken.type == TokenType.LPAREN) {
            eat(TokenType.LPAREN);
            float result = expr();
            eat(TokenType.RPAREN);
            return result;
        } else {
            throw new RuntimeException("Invalid factor");
        }
    }
}