import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Start writing the code from the next line and at the end write 'compile' to compile the output of the code");
        String text = "";
        String final_code = "";
        while (text.compareTo("compile") != 0) {
            System.out.print("> ");
            text = sc.nextLine();
            if(text.equals("compile")) {}
            else {final_code = final_code + text + " ";}
        }
        //String text = "package main import (\"fmt\") func main() {x = 3; y=5; z=(x*y)+y*6-x/2*(9/(4-x));fmt.Println(z);}";
        //String text = "package main import (\"fmt\") func main() {x=5;y=5;z=0;if(x<y){z=x-y;}elseif(x!=y){z=z+1;fmt.Println(z);}elseif(x==y){z=z+2;fmt.Println(z);}else{z=z+4;fmt.Println(z);}}";
        //String text = "package main import (\"fmt\") func main() {x=10;xz=4;y=5;z=x+(y*2);fmt.Println(x);fmt.Println(z);}";
        //String text = "package main import (\"fmt\") func main() {x = 10; y = 5; z = x + (y * 2); fmt.Println(x); fmt.Println(z); for(i=0;i<5;i=i+1){fmt.Println(i);}}";
        //String text = "package main import (\"fmt\") func main() {ans=1;for(i=1;i<=5;i=i+1){ans=ans*i;}fmt.Println(ans);}";
        Lexer lexer = new Lexer(final_code);
        Interpreter interpreter = new Interpreter(lexer);
        interpreter.interpret();

    }
}