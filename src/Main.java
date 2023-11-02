import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter an Expression: ");
        String text = sc.next();
        Lexer lexer = new Lexer(text);
        Interpreter interpreter = new Interpreter(lexer);
        int result = interpreter.expr();
        System.out.println("Result: " + result);
    }
}
