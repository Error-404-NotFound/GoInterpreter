package gointerpreter;

import java.util.List;

abstract class Expression {
    interface Visitor<R> {

        // all the necessary expression types are added here
        R visitVariableExpression(Variable expression);
        R visitAssignExpression(Assign expr);
        R visitBinaryExpression(Binary expression);
        R visitUnaryExpression(Unary exprression);
        R visitCallExpression(Call expression);
        R visitGetExpression(Get expression);
        R visitGroupingExpression(Grouping expression);
        R visitLiteralExpression(Literal expression);
        R visitLogicalExpression(Logical expression);
        R visitSetExpression(Set expression);
        R visitThisExpression(This expression);
        R visitSuperExpression(Super expression);
//        R visitIndexExpression(Index expression);
//        R visitSliceExpression(Slice expression);
//        R visitMapExpression(Map expression);
//        R visitRangeExpression(Range expression);
//        R visitYieldExpression(Yield expression);
    }

    // each expression type is added here as a subclass, they all implement the accept method, they all have a final field for each of their parameters, and they inherit the Statement class

    static class Variable extends Expression {
        Variable(Token name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpression(this);
        }

        final Token name;
    }

    static class Assign extends Expression {
        Assign(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpression(this);
        }

        final Token name;
        final Expression value;
    }

    static class Binary extends Expression {
        Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class Unary extends Expression {
        Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }

        final Token operator;
        final Expression right;
    }

    static class Call extends Expression {
        Call(Expression call, Token name, List<Expression> arguments) {
            this.call = call;
            this.name = name;
            this.arguments = arguments;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpression(this);
        }

        final Expression call;
        final Token name;
        final List<Expression> arguments;
    }

    static class Get extends Expression {
        Get(Expression object, Token name) {
            this.object = object;
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpression(this);
        }

        final Expression object;
        final Token name;
    }

    static class Grouping extends Expression {
        Grouping(Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }

        final Expression expression;
    }

    static class Literal extends Expression {
        Literal(Object literal) {
            this.literal = literal;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }

        final Object literal;
    }

    static class Logical extends Expression {
        Logical(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpression(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class Set extends Expression {
        Set(Expression object, Token name, Expression value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpression(this);
        }

        final Expression object;
        final Token name;
        final Expression value;
    }

    static class This extends Expression {
        This(Token keyword) {
            this.keyword = keyword;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpression(this);
        }

        final Token keyword;
    }

    static class Super extends Expression {
        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpression(this);
        }

        final Token keyword;
        final Token method;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
