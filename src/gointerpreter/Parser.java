package gointerpreter;

import java.util.ArrayList;
import java.util.Arrays;
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
            if(match(TokenType.VAR)) return varDeclaration();
            if(match(TokenType.FUNC)) return function("function");
            if(match(TokenType.CLASS)) return classDeclaration();
            if(match(TokenType.INITIALIZER)) return initializerDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement.Function function(String kind) {
        Token name = consume(TokenType.IDENTIFIER, "Expect "+kind+" name.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after "+kind+" name.");
        List<Token> parameters = new ArrayList<>();
        if(!check(TokenType.RIGHT_PAREN)) {
            do {
                if(parameters.size() >= 255) {
                    error(peek(), "Cannot have more than 255 parameters.");
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
            } while(match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before "+kind+" body.");
        List<Statement> body = block();
        return new Statement.Function(name, parameters, body);
    }

    private List<Statement> block() {
        List<Statement> statements = new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Statement classDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect class name.");
        Expression superclass = null;
        if(match(TokenType.LESS)) {
            consume(TokenType.IDENTIFIER, "Expect superclass name.");
            superclass = new Expression.Variable(peekPrevious());
        }
        consume(TokenType.LEFT_BRACE, "Expect '{' after class name.");
        List<Statement.Function> methods = new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");
        return new Statement.Class(name, superclass, methods);
    }

    private Statement varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        Expression initializer = null;
        if(match(TokenType.EQUAL)) {
            initializer = expression();
        }
//        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Var(name, initializer);
    }

    private Statement initializerDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
        Expression initializer=null;
        consume(TokenType.COLON, "Expect ':' after declaration of variable.");
        if(match(TokenType.EQUAL)) {
            initializer = expression();
        }
//        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.Initializer(name, initializer);
    }

    private Statement statement() {
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.RETURN)) return returnStatement();
        if(match(TokenType.IF)) return ifStatement();
        if(match(TokenType.FOR)) return forStatement();
        if(match(TokenType.WHILE)) return whileStatement();
        if(match(TokenType.LEFT_BRACE)) return new Statement.Block(block());

        return expressionStatement();
    }

    private Statement expressionStatement() {
        Expression expression = expression();
//        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Statement.ExpressionStmt(expression);
    }

    private Statement printStatement() {
        Expression value = expression();
//        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statement.Print(value);
    }

    private Statement returnStatement() {
        Token keyword = peekPrevious();
        Expression value = null;
        if(!check(TokenType.SEMICOLON)) {
            value = expression();
        }
//        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
        return new Statement.Return(keyword, value);
    }

    private Statement ifStatement() {
//        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        Expression condition = expression();
//        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");
        consume(TokenType.LEFT_BRACE, "Expect '{' before 'then block'.");
        Statement thenBranch = statement();
        consume(TokenType.RIGHT_BRACE, "Expect '}' after 'then block'.");
        Statement elseBranch = null;
        if(match(TokenType.ELSE)) {
            consume(TokenType.LEFT_BRACE, "Expect '{' before 'else block'.");
            elseBranch = statement();
            consume(TokenType.RIGHT_BRACE, "Expect '}' before 'else block'.");
        }
        return new Statement.If(condition, thenBranch, elseBranch);
    }

    private Statement forStatement() {
//        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

        Statement initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");
        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expression increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
//        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

        Statement body = statement();

        if (increment != null) {
            body = new Statement.Block(Arrays.asList(
                    body,
                    new Statement.ExpressionStmt(increment)));
        }

        if (condition == null) condition = new Expression.Literal(true);
        body = new Statement.While(condition, body);

        if (initializer != null) {
            body = new Statement.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Statement whileStatement() {
//        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        Expression condition = expression();
//        consume(TokenType.RIGHT_PAREN, "Expect ')' after 'condition'.");
        Statement body = statement();
        return new Statement.While(condition, body);
    }

    private Expression expression() {
        return assignment();
    }

    private Expression assignment() {
        Expression expression = or();
        if(match(TokenType.EQUAL)) {
            Token equals = peekPrevious();
            Expression value = assignment();
            if(expression instanceof Expression.Variable) {
                Token name = ((Expression.Variable)expression).name;
                return new Expression.Assign(name, value);
            } else if(expression instanceof Expression.Get) {
                Expression.Get get = (Expression.Get)expression;
                return new Expression.Set(get.object, get.name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expression;
    }

    private Expression or() {
        Expression expression = and();
        while(match(TokenType.OR)) {
            Token operator = peekPrevious();
            Expression right = and();
            expression = new Expression.Logical(expression, operator, right);
        }
        return expression;
    }

    private Expression and() {
        Expression expression = equality();
        while(match(TokenType.AND)) {
            Token operator = peekPrevious();
            Expression right = equality();
            expression = new Expression.Logical(expression, operator, right);
        }
        return expression;
    }

    private Expression equality() {
        Expression expression = comparison();
        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = peekPrevious();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression comparison() {
        Expression expression = addition();
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operator = peekPrevious();
            Expression right = addition();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression addition() {
        Expression expression = multiplication();
        while(match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = peekPrevious();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression multiplication() {
        Expression expression = unary();
        while(match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = peekPrevious();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }
        return expression;
    }

    private Expression unary() {
        if(match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = peekPrevious();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        return call();
    }

    private Expression call() {
        Expression expression = primary();
        while(true) {
            if(match(TokenType.LEFT_PAREN)) {
                expression = finishCall(expression);
            } else if(match(TokenType.DOT)) {
                Token name = consume(TokenType.IDENTIFIER, "Expect property name after '.'.");
                expression = new Expression.Get(expression, name);
            } else {
                break;
            }
        }
        return expression;
    }

    private Expression finishCall(Expression call) {
        List<Expression> arguments = new ArrayList<>();
        if(!check(TokenType.RIGHT_PAREN)) {
            do {
                if(arguments.size() >= 255) {
                    error(peek(), "Cannot have more than 255 arguments.");
                }
                arguments.add(expression());
            } while(match(TokenType.COMMA));
        }
        return new Expression.Call(call, consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments."), arguments);
    }

    private Expression primary() {
        if(match(TokenType.FALSE)) return new Expression.Literal(false);
        if(match(TokenType.TRUE)) return new Expression.Literal(true);
        if(match(TokenType.NIL)) return new Expression.Literal(null);
        if(match(TokenType.THIS)) return new Expression.This(peekPrevious());
        if(match(TokenType.SUPER)) {
            Token keyword = peekPrevious();
            consume(TokenType.DOT, "Expect '.' after 'super'.");
            Token method = consume(TokenType.IDENTIFIER, "Expect superclass method name.");
            return new Expression.Super(keyword, method);
        }
        if(match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.Literal(peekPrevious().literal);
        }
        if(match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(peekPrevious());
        }
        if(match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expression);
        }
        throw error(peek(), "Expect expression.");
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

    private void synchronize() {
        advance();
        while(!isAtEnd()) {
            if(peekPrevious().tokenType == TokenType.SEMICOLON) return;
            switch(peek().tokenType) {
                case CLASS:
                case FUNC:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
            }
            advance();
        }
    }
}
