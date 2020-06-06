package com.sage.graph;

import com.sage.exceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: Use GraphCharacter.isOpenBracket() and GraphCharacter.isCloseBracket() instead of relying on hard-coded parenthesis strings

public class GraphString {
    private final List<GraphCharacter> string;

    public GraphString(String rawExpression) {
        string = stringToGraphString(cleanRawExpression(rawExpression));
        cleanGraphExpression();
    }

    private GraphString(List<GraphCharacter> string) {
        this.string = new ArrayList<>();
        for(var c : string) {
            this.string.add(new GraphCharacter(c));
        }
    }

    public GraphCharacter charAt(int i) {
        return string.get(i);
    }

    public int length() {
        return string.size();
    }
    
    public GraphString getLeftOperand(int operatorIdx) {
        int[] operandIndices = getLeftOperandIndices(operatorIdx);
        return new GraphString(string.subList(operandIndices[0], operandIndices[1] + 1));
    }

    public GraphString getRightOperand(int operatorIdx) {
        int[] operandIndices = getRightOperandIndices(operatorIdx);
        return new GraphString(string.subList(operandIndices[0], operandIndices[1] + 1));
    }

    public int[] getLeftOperandIndices(int operatorIdx) {
        if(operatorIdx >= this.length() || operatorIdx < 0) {
            throwError("getLeftOperandIndices(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == 0) {
            throwError("getLeftOperandIndices(int idx) received an idx of 0. This is bad.");
        }

        if(!Operator.isOperatorChar(charAt(operatorIdx))) {
            throwError("getLeftOperandIndices(int idx) received and idx of a character which is not an operator");
        }

        int parenDepth = 0;
        for(int i = operatorIdx - 1; i >= 0; i--) {
            switch(this.charAt(i).toString()) {
                case ")" -> parenDepth--;
                case "(" -> parenDepth++;
            }

            if(parenDepth == 0) {
                return new int[] { i, operatorIdx - 1 };
            }
        }

        return new int[] { 0, operatorIdx - 1 };
    }

    public int[] getRightOperandIndices(int operatorIdx) {
        if(operatorIdx >= this.length() || operatorIdx < 0) {
            throwError("getRightOperandIndices(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == this.length() - 1) {
            throwError("getRightOperandIndices(int idx) received an idx of (exp.length() - 1)). This is bad.");
        }

        if(!Operator.isOperatorChar(charAt(operatorIdx))) {
            throwError("getRightOperandIndices(int idx) received and idx of a character which is not an operator");
        }

        int parenDepth = 0;
        for(int i = operatorIdx + 1; i < this.length(); i++) {
            switch(this.charAt(i).toString()) {
                case ")" -> parenDepth--;
                case "(" -> parenDepth++;
            }

            if(parenDepth == 0) {
                return new int[] { operatorIdx + 1, i };
            }
        }

        return new int[] { operatorIdx + 1, this.length() - 1 };
    }

    private void cleanGraphExpression() {
        addImpliedParens();

        // Parentheses must be balanced to be a valid expression
        if(!parensAreBalanced()) {
            throw new InvalidInputException("Error during preprocessing for expression \"" + toString()
                    + "\": parentheses were unbalanced.");
        }
    }

    private boolean parensAreBalanced() {
        int parenDepth = 0;
        for(var c : string) {
            switch(c.toString()) {
                case "(" -> parenDepth++;
                case ")" -> parenDepth--;
            }

            if(parenDepth < 0) {
                return false;
            }
        }

        return parenDepth == 0;
    }

    // Is allowed mutate string (only called from constructor)
    private void addImpliedParens() {
        for(Operator op : Operator.values()) {
            for(int i = string.size() - 1; i >= 0; i--) {
                if(op.hasChar(charAt(i)) && !operatorHasParens(i)) {
                    addParensAroundOperator(i);

                    // A parenthesis is going to be added somewhere before the operator. This will shift the whole string
                    // forward by one char. Because of this, we must hold i constant (rather than decreasing i by one)
                    // so that we do not miss a character. Thus, we call i++; to counteract the i--; in the for loop.
                    i++;
                }
            }
        }
    }

    // Is allowed mutate string (only called from constructor)
    private void addParensAroundOperator(int operatorIdx) {
        Operator op = Operator.fromChar(charAt(operatorIdx));
        int initialLength = length();

        // The right parenthesis must be inserted first, so that operatorIdx does not shift.

        // Right parenthesis:
        if(op.requiresRightArg) {
            for(int i = operatorIdx + 1, parenDepth = 0; i < initialLength; i++) {
                switch(charAt(i).toString()) {
                    case "(" -> parenDepth++;
                    case ")" -> parenDepth--;
                }

                if(parenDepth == 0) {
                    string.add(i + 1, new GraphCharacter(")"));
                    break;
                }
            }
        } else {
            string.add(operatorIdx + 1, new GraphCharacter(")"));
        }

        // Left parenthesis
        if(op.requiresLeftArg) {
            for(int i = operatorIdx - 1, parenDepth = 0; i >= 0; i--) {
                switch(charAt(i).toString()) {
                    case "(" -> parenDepth++;
                    case ")" -> parenDepth--;
                }

                if(parenDepth == 0) {
                    string.add(i, new GraphCharacter("("));
                    break;
                }
            }
        } else {
            string.add(operatorIdx, new GraphCharacter("("));
        }
    }

    private boolean operatorHasParens(int operatorIdx) {
        Operator op = Operator.fromChar(charAt(operatorIdx));

        if(op.requiresLeftArg) {
            int[] leftOperandIndices = getLeftOperandIndices(operatorIdx);
            if(!(leftOperandIndices[0] > 0 && charAt(leftOperandIndices[0] - 1).toString().equals("("))) {
                return false;
            }
        } else {
            if(!(operatorIdx > 0 && charAt(operatorIdx - 1).toString().equals("("))) {
                return false;
            }
        }

        if(op.requiresRightArg) {
            int[] rightOperandIndices = getRightOperandIndices(operatorIdx);
            if(!(rightOperandIndices[1] < length() - 1 && charAt(rightOperandIndices[1] + 1).toString().equals(")"))) {
                return false;
            }
        } else {
            if(!(operatorIdx < length() - 1 && charAt(operatorIdx + 1).toString().equals(")"))) {
                return false;
            }
        }

        return true;
    }

    protected InvalidInputException generateError(String message) {
        return new InvalidInputException(message + "\nInvalid expression: \"" + toString() + "\"");
    }

    protected void throwError(String message) throws InvalidInputException {
        throw generateError(message);
    }

    public int indexOfTopLevelOperator() {
        int minOperatorDepth = length();
        int minOperatorDepthIdx = -1;
        int currDepth = 0;

        for(int i = 0; i < length(); i++) {
            var c = charAt(i);

            if(c.isOperator()) {
                if(currDepth < minOperatorDepth) {
                    minOperatorDepth = currDepth;
                    minOperatorDepthIdx = i;
                }
            } else {
                switch(c.toString()) {
                    case "(" -> currDepth++;
                    case ")" -> currDepth--;
                }
            }
        }

        return minOperatorDepthIdx;
    }

    public long numVariables() {
        return string.stream().filter(GraphCharacter::isVariable).count();
    }

    public Optional<GraphCharacter> findFirstVariable() {
        return string.stream().filter(GraphCharacter::isVariable).findFirst();
    }

    @Override
    public String toString() {
        return string.stream().map(GraphCharacter::toString).collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        GraphString that = (GraphString)o;

        return Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return string != null ? string.hashCode() : 0;
    }

    private static String cleanRawExpression(String rawExpression) {
        String exp = rawExpression;

        // Clear all whitespace
        exp = exp.replaceAll("\\s+", "");

        // Empty string is an invalid expression
        if(exp.length() == 0) {
            throw new InvalidInputException("Error during preprocessing for expression \"" + exp
                    + "\": length of expression was 0.");
        }

        return exp;
    }

    private static List<GraphCharacter> stringToGraphString(String exp) {
        if(exp.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder currChar = new StringBuilder(Character.toString(exp.charAt(0)));
        List<GraphCharacter> currString = new ArrayList<>();

        if(GraphCharacter.isVariableChar(currChar.charAt(0))) {
            int i = 1;
            for(char c; i < exp.length() && GraphCharacter.isVariableChar((c = exp.charAt(i))); i++) {
                currChar.append(c);
            }
        }
        currString.add(new GraphCharacter(currChar.toString()));

        if(currChar.length() < exp.length()) {
            currString.addAll(stringToGraphString(exp.substring(currChar.length())));
        }

        return currString;
    }
}
