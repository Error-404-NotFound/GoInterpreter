package gointerpreter;

import java.lang.Integer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void>{
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expression, Integer> locals = new HashMap<>();
    Interpreter() {}
    void interpret(List<Statement> statements) {
        try {
            for (Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Main.runtimeError(error);
        }
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    private void executeBlock(List<Statement> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Statement statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    public Void visitBlockStatement(Statement.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    public Void visitClassStatement(Statement.Class statement) {
        return null;
    }

    public Void visitFunctionStatement(Statement.Function statement) {
//        Function function = new Function(statement);
        return null;
    }

    public Void visitExpressionStatement(Statement.ExpressionStmt statement) {
        evaluate(statement.expression);
        return null;
    }

    public Void visitIfStatement(Statement.If statement) {
        if (isTruth(evaluate(statement.condition))) {
            execute(statement.thenBranch);
        } else if (statement.elseBranch != null) {
            execute(statement.elseBranch);
        }
        return null;
    }

    public Void visitPrintStatement(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    public Void visitReturnStatement(Statement.Return statement) {
        Object value = null;
        if (statement.value != null) value = evaluate(statement.value);
        throw new Return(value);
    }

    public Void visitVarStatement(Statement.Var statement) {
        Object value = null;
        if (statement.initializer != null) {
            value = evaluate(statement.initializer);
        }
        environment.define(statement.name.lexeme, value);
        return null;
    }

    public Void visitForStatement(Statement.For statement) {
        if (statement.initializer != null) {
            execute(statement.initializer);
        }
        while (isTruth(evaluate(statement.condition))) {
            execute(statement.body);
            evaluate(statement.increment);
        }
        return null;
    }

    public Void visitWhileStatement(Statement.While statement) {
        while (isTruth(evaluate(statement.condition))) {
            execute(statement.body);
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////

    public Void visitBreakStatement(Statement.Break statement) {
        return null;
    }

    public Void visitContinueStatement(Statement.Continue statement) {
        return null;
    }

    public Void visitDoStatement(Statement.Do statement) {
        return null;
    }

    public Void visitSwitchStatement(Statement.Switch statement) {
        return null;
    }

    public Void visitCaseStatement(Statement.Case statement) {
        return null;
    }

    public Void visitDefaultStatement(Statement.Default statement) {
        return null;
    }

    public Void visitTryStatement(Statement.Try statement) {
        return null;
    }

    public Void visitCatchStatement(Statement.Catch statement) {
        return null;
    }

    public Void visitFinallyStatement(Statement.Finally statement) {
        return null;
    }

    public Void visitThrowStatement(Statement.Throw statement) {
        return null;
    }

    public Void visitImportStatement(Statement.Import statement) {
        return null;
    }

    public Void visitPackageStatement(Statement.Package statement) {
        return null;
    }

    public Void visitMainStatement(Statement.Main statement) {
        return null;
    }

    public Void visitWhileForDoStatement(Statement.WhileForDo statement) {
        return null;
    }

    public Void visitElseIfStatement(Statement.ElseIf statement) {
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////

    public Object visitVariableExpression(Expression.Variable expression) {
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////

    public Object visitAssignExpression(Expression.Assign expression) {
        Object value = evaluate(expression.value);
        Integer distance = locals.get(expression);
        if (distance != null) {
            environment.assignAt(distance, expression.name, value);
        } else {
            globals.assign(expression.name, value);
        }
        return value;
    }

    public Object visitBinaryExpression(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        switch (expression.operator.tokenType) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperand(expression.operator, left, right);
                return (double) left > (double) right;
            case LESS:
                checkNumberOperand(expression.operator, left, right);
                return (double) left < (double) right;
            case GREATER_EQUAL:
                checkNumberOperand(expression.operator, left, right);
                return (double) left >= (double) right;
            case LESS_EQUAL:
                checkNumberOperand(expression.operator, left, right);
                return (double) left <= (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expression.operator, "Operands must be two numbers or two strings.");
            case MINUS:
                checkNumberOperand(expression.operator, left, right);
                return (double) left - (double) right;
            case STAR:
                checkNumberOperand(expression.operator, left, right);
                return (double) left * (double) right;
            case SLASH:
                checkNumberOperand(expression.operator, left, right);
                if ((double) right == 0) throw new RuntimeError(expression.operator, "Cannot divide by zero.");
                return (double) left / (double) right;
        }
        return null;
    }

    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.right);
        switch (expression.operator.tokenType) {
            case BANG:
                return !isTruth(right);
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double) right;
            case PLUS:
                checkNumberOperand(expression.operator, right);
                return +(double) right;
        }
        return null;
    }

    public Object visitLogicalExpression(Expression.Logical expression) {
        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruth(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
}