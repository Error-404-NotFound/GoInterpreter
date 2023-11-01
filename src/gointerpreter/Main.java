package gointerpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;

public class Main {
    public static void main(String[] args) throws IOException {

    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for(;;) {
            System.out.print("> ");
        }
    }

    private static void run(String source) throws IOException {
        Scanner scanner = new Scanner(source);
        for(Token token: token) {

        }
    }
}