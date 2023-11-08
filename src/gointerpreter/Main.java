package gointerpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.util.List;

public class Main {
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        runPrompt();
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for(;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if(line==null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) throws IOException {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        for(Token token: tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        System.err.println("[line "+line+"] Error"+where+": "+message);
        hadError = true;
    }

    // polymorphism is used here for checking the type of the token
    static void error(Token token, String message) {
        if (token.tokenType == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at " + token.lexeme + "'", message);
        }
    }
}