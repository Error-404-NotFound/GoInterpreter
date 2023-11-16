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

    void executeBlock(List<Statement> statements, Environment environment) {
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

    void resolve(Expression expression, int depth) {
        locals.put(expression, depth);
    }

    public Void visitBlockStatement(Statement.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    public Void visitClassStatement(Statement.Class statement) {
        environment.define(statement.name.lexeme, null);
        Object superclass = null;
        if (statement.superclass != null) {
            superclass = evaluate(statement.superclass);
            if (!(superclass instanceof Class)) {
                throw new RuntimeError(statement.name, "Superclass must be a class.");
            }
            environment = new Environment(environment);
            environment.define("super", superclass);
        }
        Map<String, Function> methods = new HashMap<>();
        for (Statement.Function method : statement.methods) {
            Function function = new Function(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }
        Class klass = new Class(statement.name.lexeme, (Class) superclass, methods);
        if (superclass != null) {
            environment = environment.enclosing;
        }
        environment.assign(statement.name, klass);
        return null;
    }

    public Void visitFunctionStatement(Statement.Function statement) {
        Function function = new Function(statement, environment, false);
        environment.define(statement.name.lexeme, function);
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

    public Void visitInitializerStatement(Statement.Initializer statement) {
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

    ///////////////////////////////////////////////////////////////

    public Object visitVariableExpression(Expression.Variable expression) {
        return lookUpVariable(expression.name, expression);
    }

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
        Object left = evaluate(expression.left);
        if (expression.operator.tokenType == TokenType.OR) {
            if (isTruth(left)) return left;
        } else {
            if (!isTruth(left)) return left;
        }
        return evaluate(expression.right);
    }

    public Object visitCallExpression(Expression.Call expression) {
        Object call = evaluate(expression.call);
        List<Object> arguments = new java.util.ArrayList<>();
        for (Expression argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(call instanceof Callable)) {
            throw new RuntimeError(expression.name, "Can only call functions and classes.");
        }
        Callable function = (Callable) call;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expression.name, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    public Object visitGetExpression(Expression.Get expression) {
        Object object = evaluate(expression.object);
        if (object instanceof Instance) {
            return ((Instance) object).get(expression.name);
        }
        throw new RuntimeError(expression.name, "Only instances have properties.");
    }

    public Object visitSetExpression(Expression.Set expression) {
        Object object = evaluate(expression.object);
        if (!(object instanceof Instance)) {
            throw new RuntimeError(expression.name, "Only instances have fields.");
        }
        Object value = evaluate(expression.value);
        ((Instance) object).set(expression.name, value);
        return value;
    }

    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.literal;
    }

    public Object visitThisExpression(Expression.This expression) {
        return lookUpVariable(expression.keyword, expression);
    }

    public Object visitSuperExpression(Expression.Super expression) {
        int distance = locals.get(expression);
        Class superclass = (Class) environment.getAt(distance, "super");
        Instance object = (Instance) environment.getAt(distance - 1, "this");
        Function method = superclass.findMethod(object, expression.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expression.method, "Undefined property '" + expression.method.lexeme + "'.");
        }
        return method.bind(object);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object lookUpVariable(Token name, Expression expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
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
