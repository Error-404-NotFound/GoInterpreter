package gointerpreter;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {

        // all the necessary statements to be implemented in the programming language
        R visitBlockStatement(Block statement);
        R visitFunctionStatement(Function statement);
        R visitClassStatement(Class statement);
        R visitExpressionStatement(ExpressionStmt statement);
        R visitIfStatement(If statement);
        R visitPrintStatement(Print statement);
        R visitReturnStatement(Return statement);
        R visitVarStatement(Var statement);
        R visitForStatement(For statement);
        R visitWhileStatement(While statement);
    }

    // each statement is a subclass of Statement class, all of them have an accept method,  they all have a final field for each of their parameters, and they inherit the Statement class

    static class Block extends Statement {
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }

        final List<Statement> statements;
    }

    static class Function extends Statement {
        Function(Token name, List<Token> parameters, List<Statement> body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStatement(this);
        }

        final Token name;
        final List<Token> parameters;
        final List<Statement> body;
    }

    static class ExpressionStmt extends Statement {
        ExpressionStmt(Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }

        final Expression expression;
    }

    static class Class extends Statement {
        Class(Token name, Expression superclass, List<Statement.Function> methods) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStatement(this);
        }

        final Token name;
        final Expression superclass;
        final List<Statement.Function> methods;
    }

    static class If extends Statement {
        If(Expression condition, Statement thenBranch, Statement elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }

        final Expression condition;
        final Statement thenBranch;
        final Statement elseBranch;
    }

    static class Print extends Statement {
        Print(Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
        }

        final Expression expression;
    }

    static class Return extends Statement {
        Return(Token keyword, Expression value) {
            this.keyword = keyword;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStatement(this);
        }

        final Token keyword;
        final Expression value;
    }

    static class Var extends Statement {
        Var(Token name, Expression initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStatement(this);
        }

        final Token name;
        final Expression initializer;
    }

    static class For extends Statement {
        For(Statement initializer, Expression condition, Expression increment, Statement body) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStatement(this);
        }

        final Statement initializer;
        final Expression condition;
        final Expression increment;
        final Statement body;
    }

    static class While extends Statement {
        While(Expression condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }

        final Expression condition;
        final Statement body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
