
 # ${\color{lightblue}GoInterpreter}$

This repo is a project of Interpreter of Golang made in Java

## ${\color{lightblue}Versions}$
This repository has two branches representing the two versions of interpreter , Version 1 with the basic implementatons with minimal code and Version 2 with the advanced features and more organised notions in code
## ${\color{lightblue}Authors}$

- [Johri Aniket Manish](https://github.com/Error-404-NotFound)
- [Aakarsh Bansal](https://github.com/aakarsh1012bansal)
- [Siddhant Chatse Madhukar](https://github.com/sid1309)
- [Harshit Garg](https://github.com/harshit2414)

## ${\color{lightblue}Required Dependencies}$

To run this project, a system should have JDK(Java Development Kit ) and JVM(Java Virtual Machine) to execute the source files.




## ${\color{lightblue}Usage/Examples}$
Test Examples:  
```javascript
import Component from 'my-project'

function App() {
  return <Component />
}
```


Corresponding Output : 

`
  npm run test
`


## ${\color{lightblue}Explaination}$

Following are the functionalities of the implemented functions in the source code: 

- ${\color{lightgreen}Interpreter: }$ This class  helps in interpreting the expressions and statements with the help of function interpreter
- ${\color{lightgreen}Parser: }$ This class deals with the parsing the tokens futher and checks the occurence of corresponfing keywords
- ${\color{lightgreen}Scanner: }$ This class maps the token types with the keywords and adding the tokens while parsing
- ${\color{lightgreen}Statement: }$ 
- ${\color{lightgreen}Expression: }$ This abstract class helps in analyzing the expressions which are abstract with respect to the other keywords/ programming constructs
- ${\color{lightgreen}Token: }$ This class deals with token generation and correspondence with the token type
- ${\color{lightgreen}Token Type: }$ Token Type is an enum which is used to store the required keywords/token types
- ${\color{lightgreen}Environnment: }$ 
- ${\color{lightgreen}Main: }$ This is the class where the main class of the whole interpreter is present and where the instances of the class interpreter as well as other error handlings


## ${\color{lightblue}Challenges}$

Following were the main challenges faced during making Interpreter: 

- For Loop: 
  While implementing for loop,the tokens are not parsed accordingly for some iterations
- Variable Naming: 
  for the naming of variables, names as such as xyz1 is not interpreted as there is a string      and number 
- Strong Type Initialization:
  according to the syntax of Go-language, to avoid the explicit type mention while 
  initialization of a variable, a colon followed by equals is used, but interpreter is just     
  analyzing the equals keyword with the initializing keyword "var"

## ${\color{lightblue}Learnings}$

The project of creating an interpreter provided valuable insights into language design, parsing techniques, and the intricacies of translating high-level code into executable instructions.
Through the process,a deeper understanding of parsing, lexical analysis, and language semantics, enhancing the proficiency in software design and implementation is gained.

## ${\color{lightblue}Contributions}$

- Aniket : 
- Aakarsh:
- Siddhant:
- Harshit:

## ${\color{lightblue}Functions Implemented}$
- Assingment/Declaration of variables
- BODMAS rule of computing
- Control Statements
- Looping Statements

