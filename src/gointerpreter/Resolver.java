package gointerpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void>{
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD
    }

    void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Statement statement) {
        statement.accept(this);
    }

    private void resolve(Expression expression) {
        expression.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Main.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expression expression,Token token) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(token.lexeme)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Statement.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.parameters) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    public Void visitBlockStatement(Statement.Block statement) {
        beginScope();
        resolve(statement.statements);
        endScope();
        return null;
    }

    public Void visitExpressionStatement(Statement.ExpressionStmt statement) {
        resolve(statement.expression);
        return null;
    }

    public Void visitFunctionStatement(Statement.Function statement) {
        declare(statement.name);
        define(statement.name);
        resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    public Void visitClassStatement(Statement.Class statement) {
        declare(statement.name);
        define(statement.name);
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        if (statement.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(statement.superclass);
            beginScope();
            scopes.peek().put("super", true);
        }
        beginScope();
        scopes.peek().put("this", true);
        for (Statement.Function method : statement.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        endScope();
        if (statement.superclass != null) endScope();
        currentClass = enclosingClass;
        return null;
    }

    public Void visitIfStatement(Statement.If statement) {
        resolve(statement.condition);
        resolve(statement.thenBranch);
        if (statement.elseBranch != null) resolve(statement.elseBranch);
        return null;
    }

    public Void visitPrintStatement(Statement.Print statement) {
        resolve(statement.expression);
        return null;
    }

    public Void visitReturnStatement(Statement.Return statement) {
        if (currentFunction == FunctionType.NONE) {
            Main.error(statement.keyword, "Cannot return from top-level code.");
        }
        if (statement.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Main.error(statement.keyword, "Cannot return a value from an initializer.");
            }
            resolve(statement.value);
        }
        return null;
    }

    public Void visitVarStatement(Statement.Var statement) {
        declare(statement.name);
        if (statement.initializer != null) {
            resolve(statement.initializer);
        }
        define(statement.name);
        return null;
    }

    public Void visitInitializerStatement(Statement.Initializer statement) {
        if (statement.initializer != null) {
            resolve(statement.initializer);
        }
        return null;
    }

    public Void visitWhileStatement(Statement.While statement) {
        resolve(statement.condition);
        resolve(statement.body);
        return null;
    }

    public Void visitForStatement(Statement.For statement) {
        resolve(statement.initializer);
        resolve(statement.condition);
        resolve(statement.increment);
        resolve(statement.body);
        return null;
    }

    public Void visitVariableExpression(Expression.Variable expression) {
        if (!scopes.isEmpty() && scopes.peek().get(expression.name.lexeme) == Boolean.FALSE) {
            Main.error(expression.name, "Cannot read local variable in its own initializer.");
        }
        resolveLocal(expression, expression.name);
        return null;
    }

    public Void visitAssignExpression(Expression.Assign expression) {
        resolve(expression.value);
        resolveLocal(expression, expression.name);
        return null;
    }

    public Void visitBinaryExpression(Expression.Binary expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    public Void visitUnaryExpression(Expression.Unary expression) {
        resolve(expression.right);
        return null;
    }

    public Void visitCallExpression(Expression.Call expression) {
        resolve(expression.call);
        for (Expression argument : expression.arguments) {
            resolve(argument);
        }
        return null;
    }

    public Void visitGetExpression(Expression.Get expression) {
        resolve(expression.object);
        return null;
    }

    public Void visitGroupingExpression(Expression.Grouping expression) {
        resolve(expression.expression);
        return null;
    }

    public Void visitLiteralExpression(Expression.Literal expression) {
        return null;
    }

    public Void visitLogicalExpression(Expression.Logical expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    public Void visitSetExpression(Expression.Set expression) {
        resolve(expression.value);
        resolve(expression.object);
        return null;
    }

    public Void visitThisExpression(Expression.This expression) {
        if (currentClass == ClassType.NONE) {
            Main.error(expression.keyword, "Cannot use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expression, expression.keyword);
        return null;
    }

    public Void visitSuperExpression(Expression.Super expression) {
        if (currentClass == ClassType.NONE) {
            Main.error(expression.keyword, "Cannot use 'super' outside of a class.");
        } else if (currentClass!= ClassType.SUBCLASS) {
            Main.error(expression.keyword, "Cannot use 'super' in a class with no superclass.");
        }
        resolveLocal(expression, expression.keyword);
        return null;
    }
}
