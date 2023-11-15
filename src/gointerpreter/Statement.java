package gointerpreter;

import java.util.List;

abstract class Statement {
    interface Visitor<R> {

        // all the necessary statements to be implemented in the programming language
        R visitBlockStatement(Block statement);
        R visitFunctionStatement(Function statement);
        R visitExpressionStatement(ExpressionStmt statement);
        R visitClassStatement(Class statement);
        R visitIfStatement(If statement);
        R visitElseIfStatement(ElseIf statement);
        R visitSwitchStatement(Switch statement);
        R visitCaseStatement(Case statement);
        R visitDefaultStatement(Default statement);
        R visitPrintStatement(Print statement);
        R visitReturnStatement(Return statement);
        R visitVarStatement(Var statement);
        R visitForStatement(For statement);
        R visitWhileStatement(While statement);
        R visitDoStatement(Do statement);
        R visitWhileForDoStatement(WhileForDo statement);
        R visitBreakStatement(Break statement);
        R visitContinueStatement(Continue statement);
        R visitImportStatement(Import statement);
        R visitTryStatement(Try statement);
        R visitThrowStatement(Throw statement);
        R visitCatchStatement(Catch statement);
        R visitFinallyStatement(Finally statement);
        R visitPackageStatement(Package statement);
        R visitMainStatement(Main statement);

////////////////////////////////////////////////////////////////////////////////
//        if required we can implement these Statements also                  //
////////////////////////////////////////////////////////////////////////////////

//        R visitExportStatement(Export statement);
//        R visitEnumStatement(Enum statement);
//        R visitEnumValueStatement(EnumValue statement);
//        R visitTypeStatement(Type statement);
//        R visitTypeFieldStatement(TypeField statement);
//        R visitTypeMethodStatement(TypeMethod statement);
//        R visitTypeInitStatement(TypeInit statement);
//        R visitTypeConstructorStatement(TypeConstructor statement);
//        R visitTypeDestructorStatement(TypeDestructor statement);
//        R visitTypeCastStatement(TypeCast statement);
//        R visitTypeIsStatement(TypeIs statement);
//        R visitTypeAsStatement(TypeAs statement);
//        R visitTypeOfStatement(TypeOf statement);
//        R visitTypeSwitchStatement(TypeSwitch statement);
//        R visitTypeCaseStatement(TypeCase statement);
//        R visitTypeDefaultStatement(TypeDefault statement);
//        R visitTypeAliasStatement(TypeAlias statement);
//        R visitTypeInterfaceStatement(TypeInterface statement);
//        R visitTypeInterfaceMethodStatement(TypeInterfaceMethod statement);
//        R visitTypeInterfaceInitStatement(TypeInterfaceInit statement);
//        R visitTypeInterfaceConstructorStatement(TypeInterfaceConstructor statement);
//        R visitTypeInterfaceDestructorStatement(TypeInterfaceDestructor statement);
//        R visitTypeInterfaceCastStatement(TypeInterfaceCast statement);

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

    static class ElseIf extends Statement {
        ElseIf(Expression condition, Statement thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfStatement(this);
        }

        final Expression condition;
        final Statement thenBranch;
    }

    static class Switch extends Statement {
        Switch(Expression condition, List<Statement.Case> cases, Statement.Default defaultCase) {
            this.condition = condition;
            this.cases = cases;
            this.defaultCase = defaultCase;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSwitchStatement(this);
        }

        final Expression condition;
        final List<Statement.Case> cases;
        final Statement.Default defaultCase;
    }

    static class Case extends Statement {
        Case(Expression condition, List<Statement> statements) {
            this.condition = condition;
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCaseStatement(this);
        }

        final Expression condition;
        final List<Statement> statements;
    }

    static class Default extends Statement {
        Default(List<Statement> statements) {
            this.statements = statements;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDefaultStatement(this);
        }

        final List<Statement> statements;
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

    static class Do extends Statement {
        Do(Statement body, Statement.WhileForDo condition) {
            this.body = body;
            this.condition = condition;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDoStatement(this);
        }

        final Statement body;
        final Statement.WhileForDo condition;
    }

    static class WhileForDo extends Statement {
        WhileForDo(Expression condition) {
            this.condition = condition;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileForDoStatement(this);
        }

        final Expression condition;
    }

    static class Break extends Statement {
        public Expression initializer;
        public Object name;

        Break() {}

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStatement(this);
        }
    }

    static class Continue extends Statement {
        Continue() {}

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStatement(this);
        }
    }

    static class Import extends Statement {
        Import(Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitImportStatement(this);
        }

        final Expression expression;
    }

    static class Try extends Statement {
        Try(Statement body, Statement.Catch catchStatement, Statement.Finally finallyStatement) {
            this.body = body;
            this.catchStatement = catchStatement;
            this.finallyStatement = finallyStatement;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTryStatement(this);
        }

        final Statement body;
        final Statement.Catch catchStatement;
        final Statement.Finally finallyStatement;
    }

    static class Throw extends Statement {
        Throw(Expression expression) {
            this.expression = expression;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThrowStatement(this);
        }

        final Expression expression;
    }

    static class Catch extends Statement {
        Catch(Token name, Statement body) {
            this.name = name;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCatchStatement(this);
        }

        final Token name;
        final Statement body;
    }

    static class Finally extends Statement {
        Finally(Statement body) {
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFinallyStatement(this);
        }

        final Statement body;
    }

    static class Package extends Statement {
        Package(Token name) {
            this.name = name;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPackageStatement(this);
        }

        final Token name;
    }

    static class Main extends Statement {
        Main(Token name, List<Statement> body) {
            this.name = name;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMainStatement(this);
        }

        final Token name;
        final List<Statement> body;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
