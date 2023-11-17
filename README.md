
 # ${\color{rgb(0,200,100)}GoInterpreter}$

## ${\color{lightblue}About}$
This repo is a project of Interpreter of Golang made in Java.

⚠️ Note: This interpreter is complete for the purpose of evaluation but it still needs a lot of changes to be made on and can be made more dynamic. We are open to resolve the issues raised. 

## ${\color{lightblue}Versions}$
This repository has two branches representing the two versions of interpreter made by the team. Version 1 with the advanced features and more organised notions in code can be found in main branch and Version 2 with the basic implementatons with minimal code can be found in main2 branch.

All the test file and output corresponds to Version 1 and must be run for Version 1 only.

## ${\color{lightblue}Authors}$

[![](https://contrib.rocks/image?repo=Error-404-NotFound/GoInterpreter)](https://github.com/Error-404-NotFound/GoInterpreter/graphs/contributors)

- [Johri Aniket Manish](https://github.com/Error-404-NotFound)
- [Aakarsh Bansal](https://github.com/aakarsh1012bansal)
- [Viramgama Jaimin Piyush](https://github.com/i-apex)
- [Siddhant Chatse Madhukar](https://github.com/sid1309)
- [Harshit Garg](https://github.com/harshit2414)

## ${\color{lightblue}Required \space Dependencies}$

To run this project, the system should have JDK(Java Development Kit ) and JVM(Java Virtual Machine) to execute the source files.




## ${\color{lightblue}Usage/Examples}$
Input Example():  
```Golang
package main
import ("fmt")

func main() {
	var x
	var y
	var result
	var i
	x = 10
	y = 5

	fmt.Println("Performing BODMAS operations inside a for loop:")
	for i = 1; i <= 5; i=i+1 {
		result = (x + y) * (x - y + i)

		fmt.Println("Iteration : ")
		fmt.Println(i)
		fmt.Println("Result : ")
		fmt.Println(result)

		if result >= 0 {
			if result==0 {
				fmt.Println("Result is Zero")
			}
			else {
				fmt.Println("Result is Positive")
			}
		}
		else {
			fmt.Println("Result is Negative")
		}
	}
}

```


Corresponding Output : 

```
Performing BODMAS operations inside a for loop:
Iteration :
1
Result :
90
Result is Positive
Iteration :
2
Result :
105
Result is Positive
Iteration :
3
Result :
120
Result is Positive
Iteration :
4
Result :
135
Result is Positive
Iteration :
5
Result :
150
Result is Positive
```


## ${\color{lightblue}Explaination}$

Following are the major functionalities of the implemented functions in the source code(mainly corresponding to branch main):

- ${\color{lightgreen}Interpreter: }$ This class  helps in interpreting the expressions and statements with the help of function interpreter.
- ${\color{lightgreen}Parser: }$ This class deals with the parsing the tokens futher and checks the occurence of corresponfing keywords.
- ${\color{lightgreen}Scanner: }$ This class maps the token types with the keywords and adding the tokens while parsing.
- ${\color{lightgreen}Statement: }$ This class contains all the constructs of the Go programming language like print, block, if, for, etc.
- ${\color{lightgreen}Expression: }$ This abstract class helps in analyzing the expressions which are abstract with respect to the other keywords/ programming constructs.
- ${\color{lightgreen}Token: }$ This class deals with token generation and correspondence with the token type.
- ${\color{lightgreen}Token Type: }$ Token Type is an enum which is used to store the required keywords/token types.
- ${\color{lightgreen}Environnment: }$ This class is used to set environments like to assign the expression to the varible, to get the token type using the get method and many more.
- ${\color{lightgreen}Main: }$ This is the class where the main class of the whole interpreter is present and where the instances of the class interpreter as well as other error handlings.


## ${\color{lightblue}Challenges}$

Following were the main challenges faced during making Interpreter: 

- _For Loop:_ 
  While implementing for loop,the tokens are not parsed accordingly for some iterations
- _Variable Naming:_ 
  for the naming of variables, names as such as xyz1 is not interpreted as there is a string and number 
- _Strong Type Initialization:_
  according to the syntax of Go-language, to avoid the explicit type mention while 
  initialization of a variable, a colon followed by equals is used, but interpreter is just     
  analyzing the equals keyword with the initializing keyword "var"
- _Coordination with Team:_
  Since most of us had other commitments to do, sometimes we were unable to communicate with each other.

## ${\color{lightblue}Learnings}$

The project of creating an interpreter provided valuable insights into language design, parsing techniques, and the intricacies of translating high-level code into executable instructions.
Through the process,a deeper understanding of parsing, lexical analysis, and language semantics, enhancing the proficiency in software design and implementation is gained.

## ${\color{lightblue}Contributions}$

The following are the major contributions by the following members and other minor contributions were made collectively:

- Aniket    : worked on Parser.java, Interpreter.java, Environment.java, Resolver.java, Scanner.java, Main.java, Debugging and other commits in other files.
- Aakarsh   : worked on Scanner.java, Token.java, TokenType.java, Expression.java, Parser.java, Interpreter.java, Debugging, and Version 2.
- Jaimin    : worked on Callable.java, Function.java, Instance.java, Class.java, Parser.java and made the README file.
- Siddhant  : worked on RuntimeError.java, Return.java, made the testfiles, Token.java, Debugging.
- Harshit   : worked on Statement.java, Class.java, made the README file, Callable.java and Version 2.

## ${\color{lightblue}Functions \space Implemented}$
- Assingment/Declaration of variables
- BODMAS rule of computing
- Control Statements
- Looping Statements

