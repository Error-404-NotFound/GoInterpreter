package gointerpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.util.List;

public class Main {
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
        }
    }

    private static void run(String source) throws IOException {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        for(Token token: tokens) {
            System.out.println(token);
        }
    }
}