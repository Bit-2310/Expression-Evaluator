/* PROJECT - 1 : APPLICATIONS OF QUEUES AND STACKS
 * SUBMITTED BY : PRANAVA UPPARLAPALLI
 * NET-ID : pxu230006
 * DATE : 02/22/2024
 */

package pxu230006;

import java.io.File;
import java.io.FileNotFoundException; // Used to throwing exceptions where it cannot input a file
import java.util.ArrayDeque; // Used to make stacks
import java.util.Iterator; // Imported to Iterate through various lists throughout the program
import java.util.LinkedList; // Used for the Linked List of tokens
/* Importing required Libraries for the project*/
import java.util.List; // This interface is used to store the list of tokens
import java.util.Scanner; // Taken for user input from the console or a file

// TODO: Auto-generated Javadoc
/**
 *  Class to store a node of expression tree
 *     For each internal node, element contains a binary operator
 *     List of operators: +|*|-|/|%|^
 *     Other tokens: (|)
 *     Each leaf node contains an operand (long integer).
 */

public class Expression {
	
	/**
	 * The Enum TokenType.
	 */
	//ENUM is used cause it can represent various tokens that can be used in an expression
    public enum TokenType {  
  /** The plus. */
  // NIL is a special token that can be used to mark bottom of stack
    	PLUS, 
 /** The times. */
 TIMES, 
 /** The minus. */
 MINUS, 
 /** The div. */
 DIV, 
 /** The mod. */
 MOD, 
 /** The power. */
 POWER, 
 /** The open. */
 OPEN, 
 /** The close. */
 CLOSE, 
 /** The nil. */
 NIL, 
 /** The number. */
 NUMBER
    }
    
    /**
     * The Class Token.
     */
    public static class Token {
		
		/** The token. */
		TokenType token;
		
		/** The priority. */
		int priority; // for precedence of operator
		
		/** The number. */
		Long number;  // used to store number of token = NUMBER
		
		/** The string. */
		String string;

	/**
	 * Instantiates a new token.
	 *
	 * @param op the op
	 * @param pri the pri
	 * @param tok the tok
	 */
	Token(TokenType op, int pri, String tok) {// It is a constructor for used for each type of Token
	    token = op; 
	    priority = pri;
	    number = null;
	    string = tok;
	}

	// It is Constructor for number. 
	/**
	 * Instantiates a new token.
	 *
	 * @param tok the tok
	 */
	// It is called when other options have been exhausted.
	Token(String tok) {
	    token = TokenType.NUMBER;
	    number = Long.parseLong(tok);
	    string = tok;
		}
	
	/**
	 * Checks if is operand.
	 *
	 * @return true, if is operand
	 */
	// Checks if a token is an operand
	boolean isOperand() { 
		return token == TokenType.NUMBER;
		}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	//returns numerical value
	public long getValue() {
	    return isOperand() ? number : 0;
		}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	//returns string representation of the tokens
	public String toString() { 
		return string;
		}
    }

    /** The element. */
    Token element;
    
    /** The right. */
    Expression left, right; //References to left and right sub expression
    
//[METHOD - 1]: 
    // Create token corresponding to a string
    // tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
    /**
 * Gets the token.
 *
 * @param tok the tok
 * @return the token
 */
// NUMBER is either "0" or "[-]?[1-9][0-9]*
    static Token getToken(String tok) {  // To do
	Token result;// The resulting Token object
	// Determining the type of token based on the input string
	switch(tok) {
	case "(":
		result = new Token(TokenType.OPEN, 0, tok);
		break;
	case ")":
		result = new Token(TokenType.CLOSE, 0, tok);
		break;	
	case "+":
	    result = new Token(TokenType.PLUS, 1, tok); 
	    break;
	case "-":
		result = new Token(TokenType.MINUS, 1 , tok);
		break;
	case "^":
		result = new Token(TokenType.POWER, 2, tok);
		break;
	case "/":
		result = new Token(TokenType.DIV, 3, tok);
		break;
	case "%":
		result = new Token(TokenType.MOD, 3, tok);
		break;
	case "*":
		result = new Token(TokenType.TIMES, 3, tok);
		break;

	default:
	    result = new Token(tok); // Default to NUMBER token for operand strings
	    break;
	}
	return result; // Returning the parsed Token objects
    }
    
    /**
     * Instantiates a new expression.
     */
    private Expression() {
    	element = null;
    }
    
    /**
     * Instantiates a new expression.
     *
     * @param oper the oper
     * @param left the left
     * @param right the right
     */
    private Expression(Token oper, Expression left, Expression right) {
    	this.element = oper;
    	this.left = left;
    	this.right = right;
    }

    /**
     * Instantiates a new expression.
     *
     * @param num the num
     */
    private Expression(Token num) {
		this.element = num;
		this.left = null;
		this.right = null;
    }
//[METHOD - 2:]
    // Given a list of tokens corresponding to an infix expression,
    /**
 * Infix to expression.
 *
 * @param exp the exp
 * @return the expression
 */
// return the expression tree corresponding to it.
    public static Expression infixToExpression(List<Token> exp) {  // To do
    // Using ArrayDeque Library to create our own STACK which is called the operator````
        ArrayDeque<Token> operator = new ArrayDeque<>();
      //NIL is used to mark the bottom of the stack
        operator.push(new Token(TokenType.NIL, 0, "|"));
        ArrayDeque<Expression> operatorStack = new ArrayDeque<>();
        int precedence = 1; // Initial value for precedence
        //Using for loop to parse through the stack 
        for (Token token : exp) { //iterates through tokens within List<Token> exp
            //Condition : if token is a number
            if (token.isOperand()){
                operatorStack.push(new Expression(token));
            }
            else {// if token is operator
            	// Pop operators from the stack and create expression trees until an opening parenthesis is found
                if(token.token == TokenType.OPEN) {
                    //increasing the precedence so inside the () get looked at first
                    precedence *=  10;
                    operator.push(token);
                }
                else if (token.token == TokenType.CLOSE) {
                	// Pop operators from the stack and create expression trees until an opening parenthesis is found
                    while(operator.peek().token != TokenType.OPEN){
                        Expression right = operatorStack.pop();
                        Expression left = operatorStack.pop();
                        Expression tree = new Expression(operator.pop(), left, right);
                        operatorStack.push(tree);
                    }
                    operator.pop();// Pop the opening parenthesis from the stack
                }
                //Condition: If the token's priority times precedence is less than or equal to the top operator's priority
                else if (token.priority * precedence <= operator.peek().priority) {
                    while(operator.peek().priority >= token.priority * precedence){
                    	// Pop operators from the stack and create expression trees based on precedence
                        Expression right = operatorStack.pop();
                        Expression left = operatorStack.pop();
                        Expression tree = new Expression(operator.pop(), left, right);
                        operatorStack.push(tree);
                    }
                    token.priority = token.priority * precedence;
                    operator.push(token);// Pushing the tokens onto the operator stack
                }
                else {
                    token.priority = token.priority * precedence;
                    operator.push(token); // Pushing the tokens onto the operator stack
                }
            }
        }
     // Creating the final expression tree by combining the remaining operators and operands
        while(operator.peek().token != TokenType.NIL) {
            Expression right = operatorStack.pop();
            Expression left;
            // Creating a dummy expression with a 0 token if there is no room in left
            if(operatorStack.isEmpty()){
                left = new Expression(new Token("0"));
            }
            else{
                left = operatorStack.pop();
            }
            Expression tree = new Expression(operator.pop(), left, right);
            operatorStack.push(tree);
        }
        return operatorStack.pop(); // The root of the expression tree
    }
//[METHOD - 3]:
    // Given a list of tokens corresponding to an infix expression,
    /**
 * Infix to postfix.
 *
 * @param exp the exp
 * @return the list
 */
// return its equivalent post fix expression as a list of tokens.
    public static List<Token> infixToPostfix(List<Token> exp) { 
    	ArrayDeque<Token> operator = new ArrayDeque<>(); // Creating a new operator
        operator.push(new Token(TokenType.NIL, 0 , "|")); // Push NIL token onto the stack to mark the bottom
        List<Token> postFix = new LinkedList<>(); // Using a Linked List to store the postfix expression
        int precedence =  1; // Initial precedence value
        
        // Iterate through each token in the infix expression
        for (Token token : exp) { 
            if(token.isOperand()){// If the token is an operand, add it to the postfix expression
                postFix.add(token);
            }
            else{
                if(token.token == TokenType.OPEN){ 
                	// If token is operator checking if its '(' or ')' and increasing their precedence
                    precedence *= 10;
                    operator.push(token); // Push the token onto the operator stack
                } else if (token.token == TokenType.CLOSE) { // If the token is a closing parenthesis
                    while(operator.peek().token != TokenType.OPEN){
                    	/* Pop operators from the stack and add them to the 
                    	postfix expression until an opening parenthesis is found */
                        postFix.add(operator.pop());
                    }
                    operator.pop();
                    precedence /= 10;
                }
                else if (token.priority * precedence <= operator.peek().priority) {
                	// Pop operators from the stack and add them to the post fix expression based on precedence
                    while(operator.peek().priority >= token.priority * precedence){
                        postFix.add(operator.pop());
                    }
                    token.priority = token.priority * precedence; // Adjusting the token's priority based on precedence
                    operator.push(token); // Pushing the token onto the operator stack
                }
                else{
                    token.priority = token.priority * precedence; 
                    operator.push(token);
                }
            }
        }
     // Adding the remaining operators from the stack 	
        while(operator.peek().token != TokenType.NIL){
            postFix.add(operator.pop()); // popping the remaining operators
        }
        return postFix;	 //Returning the Post Fix expression
        }

//[METHOD - 4]:
    /**
 * Evaluate postfix.
 *
 * @param exp the exp
 * @return the long
 */
// Given a post fix expression, evaluate it and return its value.
    public static long evaluatePostfix(List<Token> exp) {
    	Iterator<Token> iter = exp.iterator(); // Iterator to traverse tokens in postfix expression to evaluate it
        ArrayDeque<Token> operand = new ArrayDeque<>(); // Creating a new stack named Operand to store in a stack
        // Iterating through each token using while loop
        while(iter.hasNext()){
            Token token = iter.next(); // assigning the next token
            if(token.isOperand()){ // Depending if token is operand it will be added into operand stack
                operand.push(token);
            }
            else{
                if(operand.size() < 2){ // checking no of operands in a stack
                    System.out.println("Number of operands are inequal");
                    return 0;
                }
                long right = operand.pop().number; // Popping the numbers from stack
                if(operand.isEmpty()){ // Ensure the stack is not empty after popping the right operand
                    System.out.println("Parenthesis Error");
                    return 0;
                }
                long left = operand.pop().number;// Popping the numbers from stack
                long result = 0; // Variable to store the result of the operation
                //return the result of the operation
                switch (token.token){
                    case PLUS:
                        result = left + right;
                        break;
                    case MINUS:
                        result = left - right;
                        break;
                    case TIMES:
                        result = left * right;
                        break;
                    case DIV:
                        result = left / right;
                        break;
                    case MOD:
                        result = left % right;
                        break;
                    case POWER:
                        result = left ^ right;
                        break;
                    default:
                    	break;
                }
                operand.push(new Token("" + result));// Pushing the result back onto the operand stack
            }
        }
     // Checking if the stack contains exactly one operand after evaluation
        if(operand.size() != 1){
            System.out.println("Expression is not valid");
            return 0;
        }
        return operand.pop().number; // Return the final result of the expression evaluation
    }


//[METHOD - 5]:
    /**
 * Evaluate expression.
 *
 * @param tree the tree
 * @return the long
 */
// Given an expression tree, evaluate it and return its value.
    public static long evaluateExpression(Expression tree) {  // To do
        //Checking if tree has no leaf nodes
        if(tree.left == null && tree.right == null){
            return tree.element.number;
        }
        else{/*Evaluating and assigning left and right expression trees*/
            long result = 0;
            long left = evaluateExpression(tree.left);
            long right = evaluateExpression(tree.right);
            TokenType operator = tree.element.token; //Operator of currentNode
            //return the result of the operation
            // Handling Division by 0.  
            if ((operator == TokenType.DIV || operator == TokenType.MOD) && right == 0) {
                throw new ArithmeticException("Division by zero");
            }
            //Operators checking
            switch(tree.element.token){
                case PLUS:
                    result = left + right;
                    break;
                case TIMES:
                    result = left * right;
                    break;
                case MINUS:
                    result = left - right;
                    break;
                case DIV:
                    result = left / right;
                    break;
                case MOD:
                    result = left % right;
                    break;
                case POWER:
                    result = left ^ right;
                    break;
                default:
                    result = 0; //setting default to 0
                    break;
            }
            return result; // Returning the result of equation 
            }
        }

/**
 * The main method.
 *
 * @param args the arguments
 * @throws FileNotFoundException the file not found exception
 */
//[MAIN METHOD]:
	@SuppressWarnings("resource")
	public static void main(String[] args) throws FileNotFoundException {
	Scanner in;
	if (args.length > 0) {
	    File inputFile = new File(args[0]);
	    in = new Scanner(inputFile);
	} else {
	    in = new Scanner(System.in);
	}

	int count = 0;
	while(in.hasNext()) {
	    String s = in.nextLine();
	    List<Token> infix = new LinkedList<>();
	    Scanner sscan = new Scanner(s);
	    int len = 0;
	    while(sscan.hasNext()) {
		infix.add(getToken(sscan.next()));
		len++;
	    }
	    if(len > 0) {
		count++;
		System.out.println("Expression number: " + count);
		System.out.println("Infix expression: " + infix);
		Expression exp = infixToExpression(infix);
		List<Token> post = infixToPostfix(infix);
		System.out.println("Postfix expression: " + post);
		long pval = evaluatePostfix(post);
		long eval = evaluateExpression(exp);
		System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
	    }
	}
    }
}