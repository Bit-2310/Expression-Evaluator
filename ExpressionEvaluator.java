package pxu230006;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class provides functionality to evaluate mathematical expressions.
 * It can convert infix expressions to postfix, build expression trees,
 * and evaluate expressions using both postfix notation and expression trees.
 */
public class ExpressionEvaluator {

    /**
     * Enum representing different types of tokens in an expression.
     */
    public enum TokenType {
        /** The plus operator. */
        PLUS,
        /** The times operator. */
        TIMES,
        /** The minus operator. */
        MINUS,
        /** The division operator. */
        DIV,
        /** The modulo operator. */
        MOD,
        /** The power operator. */
        POWER,
        /** The open parenthesis. */
        OPEN,
        /** The close parenthesis. */
        CLOSE,
        /** The nil token. */
        NIL,
        /** The number token. */
        NUMBER
    }

    /**
     * Represents a token in the expression.
     */
    public static class Token {
        TokenType type;
        int priority;
        Long number;
        String string;

        /**
         * Constructor for operator tokens.
         * @param type The type of the token
         * @param priority The priority of the operator
         * @param string The string representation of the token
         */
        Token(TokenType type, int priority, String string) {
            this.type = type;
            this.priority = priority;
            this.number = null;
            this.string = string;
        }

        /**
         * Constructor for number tokens.
         * @param tok The string representation of the number
         */
        Token(String tok) {
            this.type = TokenType.NUMBER;
            this.priority = 0;
            this.number = Long.parseLong(tok);
            this.string = tok;
        }

        /**
         * Checks if the token is an operand (number).
         * @return true if the token is a number, false otherwise
         */
        boolean isOperand() {
            return type == TokenType.NUMBER;
        }

        /**
         * Gets the value of the token if it's a number.
         * @return the value of the number token, or 0 if it's not a number
         */
        long getValue() {
            return isOperand() ? number : 0;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    /**
     * Represents a node in the expression tree.
     */
    private static class ExpressionTree {
        Token element;
        ExpressionTree left, right;

        /**
         * Constructor for an internal node of the expression tree.
         * @param element The token at this node
         * @param left The left subtree
         * @param right The right subtree
         */
        ExpressionTree(Token element, ExpressionTree left, ExpressionTree right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }

        /**
         * Constructor for a leaf node (number) of the expression tree.
         * @param number The number token
         */
        ExpressionTree(Token number) {
            this.element = number;
            this.left = null;
            this.right = null;
        }
    }

    private static final Map<TokenType, Integer> PRECEDENCE = new EnumMap<>(TokenType.class);
    static {
        PRECEDENCE.put(TokenType.PLUS, 1);
        PRECEDENCE.put(TokenType.MINUS, 1);
        PRECEDENCE.put(TokenType.TIMES, 2);
        PRECEDENCE.put(TokenType.DIV, 2);
        PRECEDENCE.put(TokenType.MOD, 2);
        PRECEDENCE.put(TokenType.POWER, 3);
    }

    /**
     * Converts a string to a Token object.
     * @param tok The string representation of the token
     * @return The corresponding Token object
     */
    public static Token getToken(String tok) {
        return switch (tok) {
            case "(" -> new Token(TokenType.OPEN, 0, tok);
            case ")" -> new Token(TokenType.CLOSE, 0, tok);
            case "+" -> new Token(TokenType.PLUS, PRECEDENCE.get(TokenType.PLUS), tok);
            case "-" -> new Token(TokenType.MINUS, PRECEDENCE.get(TokenType.MINUS), tok);
            case "*" -> new Token(TokenType.TIMES, PRECEDENCE.get(TokenType.TIMES), tok);
            case "/" -> new Token(TokenType.DIV, PRECEDENCE.get(TokenType.DIV), tok);
            case "%" -> new Token(TokenType.MOD, PRECEDENCE.get(TokenType.MOD), tok);
            case "^" -> new Token(TokenType.POWER, PRECEDENCE.get(TokenType.POWER), tok);
            default -> new Token(tok);
        };
    }

    /**
     * Converts an infix expression to an expression tree.
     * @param exp The list of tokens in infix notation
     * @return The root of the expression tree
     */
    public static ExpressionTree infixToExpression(List<Token> exp) {
        Deque<Token> operators = new ArrayDeque<>();
        Deque<ExpressionTree> operands = new ArrayDeque<>();
        operators.push(new Token(TokenType.NIL, 0, "|"));
        int precedence = 1;

        for (Token token : exp) {
            if (token.isOperand()) {
                operands.push(new ExpressionTree(token));
            } else {
                if (token.type == TokenType.OPEN) {
                    precedence *= 10;
                    operators.push(token);
                } else if (token.type == TokenType.CLOSE) {
                    while (operators.peek().type != TokenType.OPEN) {
                        ExpressionTree right = operands.pop();
                        ExpressionTree left = operands.pop();
                        operands.push(new ExpressionTree(operators.pop(), left, right));
                    }
                    operators.pop();
                    precedence /= 10;
                } else {
                    while (operators.peek().type != TokenType.NIL && 
                           token.priority * precedence <= operators.peek().priority) {
                        ExpressionTree right = operands.pop();
                        ExpressionTree left = operands.pop();
                        operands.push(new ExpressionTree(operators.pop(), left, right));
                    }
                    token.priority *= precedence;
                    operators.push(token);
                }
            }
        }

        while (operators.peek().type != TokenType.NIL) {
            ExpressionTree right = operands.pop();
            ExpressionTree left = operands.isEmpty() ? new ExpressionTree(new Token("0")) : operands.pop();
            operands.push(new ExpressionTree(operators.pop(), left, right));
        }

        return operands.pop();
    }

    /**
     * Converts an infix expression to postfix notation.
     * @param exp The list of tokens in infix notation
     * @return The list of tokens in postfix notation
     */
    public static List<Token> infixToPostfix(List<Token> exp) {
        Deque<Token> operators = new ArrayDeque<>();
        List<Token> postfix = new LinkedList<>();
        operators.push(new Token(TokenType.NIL, 0, "|"));
        int precedence = 1;

        for (Token token : exp) {
            if (token.isOperand()) {
                postfix.add(token);
            } else {
                if (token.type == TokenType.OPEN) {
                    precedence *= 10;
                    operators.push(token);
                } else if (token.type == TokenType.CLOSE) {
                    while (operators.peek().type != TokenType.OPEN) {
                        postfix.add(operators.pop());
                    }
                    operators.pop();
                    precedence /= 10;
                } else {
                    while (operators.peek().type != TokenType.NIL && 
                           token.priority * precedence <= operators.peek().priority) {
                        postfix.add(operators.pop());
                    }
                    token.priority *= precedence;
                    operators.push(token);
                }
            }
        }

        while (operators.peek().type != TokenType.NIL) {
            postfix.add(operators.pop());
        }

        return postfix;
    }

    /**
     * Evaluates a postfix expression.
     * @param exp The list of tokens in postfix notation
     * @return The result of the expression evaluation
     * @throws IllegalArgumentException if the expression is invalid
     * @throws ArithmeticException if there's a division by zero
     */
    public static long evaluatePostfix(List<Token> exp) {
        Deque<Long> operands = new ArrayDeque<>();

        for (Token token : exp) {
            if (token.isOperand()) {
                operands.push(token.number);
            } else {
                if (operands.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression: insufficient operands");
                }
                long right = operands.pop();
                long left = operands.pop();
                long result = switch (token.type) {
                    case PLUS -> left + right;
                    case MINUS -> left - right;
                    case TIMES -> left * right;
                    case DIV -> {
                        if (right == 0) throw new ArithmeticException("Division by zero");
                        yield left / right;
                    }
                    case MOD -> {
                        if (right == 0) throw new ArithmeticException("Modulo by zero");
                        yield left % right;
                    }
                    case POWER -> (long) Math.pow(left, right);
                    default -> throw new IllegalArgumentException("Unknown operator: " + token.type);
                };
                operands.push(result);
            }
        }

        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid expression: too many operands");
        }
        return operands.pop();
    }

    /**
     * Evaluates an expression tree.
     * @param tree The root of the expression tree
     * @return The result of the expression evaluation
     * @throws ArithmeticException if there's a division by zero
     * @throws IllegalArgumentException if there's an unknown operator
     */
    public static long evaluateExpression(ExpressionTree tree) {
        if (tree.left == null && tree.right == null) {
            return tree.element.number;
        }

        long left = evaluateExpression(tree.left);
        long right = evaluateExpression(tree.right);

        return switch (tree.element.type) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case TIMES -> left * right;
            case DIV -> {
                if (right == 0) throw new ArithmeticException("Division by zero");
                yield left / right;
            }
            case MOD -> {
                if (right == 0) throw new ArithmeticException("Modulo by zero");
                yield left % right;
            }
            case POWER -> (long) Math.pow(left, right);
            default -> throw new IllegalArgumentException("Unknown operator: " + tree.element.type);
        };
    }

    /**
     * Main method to run the expression evaluator.
     * It reads expressions from a file or standard input, evaluates them,
     * and prints the results.
     *
     * @param args Command line arguments (optional file path)
     */
    public static void main(String[] args) {
        try (Scanner in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(System.in)) {
            int count = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                List<Token> infix = new LinkedList<>();
                try (Scanner lineScanner = new Scanner(line)) {
                    while (lineScanner.hasNext()) {
                        infix.add(getToken(lineScanner.next()));
                    }
                }

                if (!infix.isEmpty()) {
                    count++;
                    System.out.println("Expression number: " + count);
                    System.out.println("Infix expression: " + infix);

                    ExpressionTree exp = infixToExpression(infix);
                    List<Token> postfix = infixToPostfix(infix);

                    System.out.println("Postfix expression: " + postfix);

                    try {
                        long postfixEval = evaluatePostfix(postfix);
                        long treeEval = evaluateExpression(exp);
                        System.out.println("Postfix eval: " + postfixEval + " Tree eval: " + treeEval);
                    } catch (ArithmeticException | IllegalArgumentException e) {
                        System.out.println("Error evaluating expression: " + e.getMessage());
                    }
                    System.out.println();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }
}