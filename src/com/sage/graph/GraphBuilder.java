package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.*;

class GraphBuilder {
    public static OUTPUT build(String rawExpression) {
        return new OUTPUT(_build(preprocessExpression(rawExpression)));
    }

    private static Node _build(String exp) throws InvalidInputException {
        int topOperatorIdx = indexOfTopLevelOperator(exp);
        if(topOperatorIdx == -1) {
            String cleanedExp = removeOuterParens(exp);

            // If the expression has no operators and is only one character, it must be a variable
            if(cleanedExp.length() == 1 && isVariableChar(cleanedExp.charAt(0))) {
                return new NAMED_INPUT_PIPE(cleanedExp);
            } else { // If the expression has no operators and is more than one character, it's invalid
                throwError(cleanedExp, "Expression was only one non-variable character.");
            }
        }

        char topOperator = exp.charAt(topOperatorIdx);
        if(Operator.isNOT(topOperator)) {
            return new NOT(
                    _build(getRightOperand(exp, topOperatorIdx)));
        } else if(Operator.isAND(topOperator)) {
            return new AND(
                    _build(getLeftOperand(exp, topOperatorIdx)),
                    _build(getRightOperand(exp, topOperatorIdx)));
        } else if(Operator.isOR(topOperator)) {
            return new OR(
                    _build(getLeftOperand(exp, topOperatorIdx)),
                    _build(getRightOperand(exp, topOperatorIdx)));
        }

        throwError(exp, "the top operator index was >= 0, but no operator was found. This is bad.");
        return null;
    }

    private static String preprocessExpression(String rawExpression) {
        String exp = rawExpression;

        // Clear all whitespace
        exp = exp.replaceAll("\\s+", "");

        // Empty string is an invalid expression
        if(exp.length() == 0) {
            throwError(exp, "length of expression was 0.");
        }

        exp = addImpliedParens(exp);

        // This line was removed because it fails for expressions such as "(A&B)|(~C)".
        // Instead, extra parentheses are removed when no operators are found in the expression
//        exp = removeOuterParens(exp);

        // Parentheses must be balanced to be a valid expression
        if(!parensAreBalanced(exp)) {
            throwError(exp, "parentheses were unbalanced.");
        }

        return exp;
    }

    private static String getLeftOperand(String exp, int operatorIdx) {
        int[] operandIndices = getLeftOperandIndices(exp, operatorIdx);
        return exp.substring(operandIndices[0], operandIndices[1] + 1);
    }

    private static String getRightOperand(String exp, int operatorIdx) {
        int[] operandIndices = getRightOperandIndices(exp, operatorIdx);
        return exp.substring(operandIndices[0], operandIndices[1] + 1);
    }

    private static int[] getLeftOperandIndices(String exp, int operatorIdx) {
        if(operatorIdx >= exp.length() || operatorIdx < 0) {
            throwError(exp, "getLeftOperand(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == 0) {
            throwError(exp, "getLeftOperand(int idx) received an idx of 0. This is bad.");
        }

        int parenDepth = 0;
        for(int i = operatorIdx - 1; i >= 0; i--) {
            switch(exp.charAt(i)) {
                case ')' -> parenDepth--;
                case '(' -> parenDepth++;
            }

            if(parenDepth == 0) {
                return new int[] { i, operatorIdx - 1 };
            }
        }

        return new int[] { 0, operatorIdx - 1 };
    }

    private static int[] getRightOperandIndices(String exp, int operatorIdx) {
        if(operatorIdx >= exp.length() || operatorIdx < 0) {
            throwError(exp, "getRightOperand(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == exp.length() - 1) {
            throwError(exp, "getRightOperand(int idx) received an idx of (exp.length() - 1)). This is bad.");
        }

        int parenDepth = 0;
        for(int i = operatorIdx + 1; i < exp.length(); i++) {
            switch(exp.charAt(i)) {
                case ')' -> parenDepth--;
                case '(' -> parenDepth++;
            }

            if(parenDepth == 0) {
                return new int[] { operatorIdx + 1, i };
            }
        }

        return new int[] { operatorIdx + 1, exp.length() - 1 };
    }

    private static boolean parensAreBalanced(String exp) {
        int parenDepth = 0;
        for(char c : exp.toCharArray()) {
            switch(c) {
                case '(' -> parenDepth++;
                case ')' -> parenDepth--;
            }

            if(parenDepth < 0) {
                return false;
            }
        }

        return parenDepth == 0;
    }

    private static String removeOuterParens(String initialExp) {
        String exp = initialExp;

        while(exp.length() >= 2
                && exp.charAt(0) == '('
                && exp.charAt(exp.length() - 1) == ')') {
            exp = exp.substring(1, exp.length() - 1);
        }

        return exp;
    }

    private static String addImpliedParens(String initialExp) {
        String exp = initialExp;

        for(Operator op : Operator.values()) {
            for(int i = initialExp.length() - 1; i >= 0; i--) {
                if(op.hasChar(exp.charAt(i)) && !operatorHasParens(exp, i)) {
                    exp = addParensToOperator(exp, i);

                    // A parenthesis is going to be added somewhere before the operator. This will shift the whole string
                    // forward by one char. Because of this, we must hold i constant (rather than decreasing i by one)
                    // so that we do not miss a character. Thus, we call i++; to counteract the i--; in the for loop.
                    i++;
                }
            }
        }

        return exp;
    }

    private static String addParensToOperator(String initialExp, int operatorIdx) {
        Operator op = Operator.fromChar(initialExp.charAt(operatorIdx));

        var expBuilder = new StringBuilder(initialExp);

        // The right parenthesis must be inserted first, so that operatorIdx does not shift.

        // Right parenthesis:
        if(op.requiresRightArg) {
            for(int i = operatorIdx + 1, parenDepth = 0; i < expBuilder.length(); i++) {
                char c = expBuilder.charAt(i);
                switch(c) {
                    case '(' -> parenDepth++;
                    case ')' -> parenDepth--;
                }

                if(parenDepth == 0) {
                    expBuilder.insert(i + 1, ')');
                    break;
                }
            }
        } else {
            expBuilder.insert(operatorIdx + 1, ')');
        }

        // Left parenthesis
        if(op.requiresLeftArg) {
            for(int i = operatorIdx - 1, parenDepth = 0; i >= 0; i--) {
                char c = expBuilder.charAt(i);
                switch(c) {
                    case '(' -> parenDepth++;
                    case ')' -> parenDepth--;
                }

                if(parenDepth == 0) {
                    expBuilder.insert(i, '(');
                    break;
                }
            }
        } else {
            expBuilder.insert(operatorIdx, '(');
        }

        return expBuilder.toString();
    }

    private static boolean operatorHasParens(String exp, int operatorIdx) {
        Operator op = Operator.fromChar(exp.charAt(operatorIdx));

        if(op.requiresLeftArg) {
            int[] leftOperandIndices = getLeftOperandIndices(exp, operatorIdx);
            if(!(leftOperandIndices[0] > 0 && exp.charAt(leftOperandIndices[0] - 1) == '(')) {
                return false;
            }
        } else {
            if(!(operatorIdx > 0 && exp.charAt(operatorIdx - 1) == '(')) {
                return false;
            }
        }

        if(op.requiresRightArg) {
            int[] rightOperandIndices = getRightOperandIndices(exp, operatorIdx);
            if(!(rightOperandIndices[1] < exp.length() - 1 && exp.charAt(rightOperandIndices[1] + 1) == ')')) {
                return false;
            }
        } else {
            if(!(operatorIdx < exp.length() - 1 && exp.charAt(operatorIdx + 1) == ')')) {
                return false;
            }
        }

        return true;
    }

    private static int indexOfTopLevelOperator(String exp) {
        int minOperatorDepth = exp.length();
        int minOperatorDepthIdx = -1;
        int currDepth = 0;

        for(int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);

            if(Operator.isOperator(c)) {
                if(currDepth < minOperatorDepth) {
                    minOperatorDepth = currDepth;
                    minOperatorDepthIdx = i;
                }
            } else {
                switch(c) {
                    case '(' -> currDepth++;
                    case ')' -> currDepth--;
                }
            }
        }

        return minOperatorDepthIdx;
    }

    private static boolean isVariableChar(char c) {
        return Character.isAlphabetic(c);
    }

    private static void throwError(String exp, String message) throws InvalidInputException {
        throw new InvalidInputException("Error: " + message + "\nInvalid expression: \"" + exp + "\"");
    }

    public enum Operator {
        NOT(new char[] { '~', '!' }, false, true),
        AND(new char[] { '&' }, true, true),
        OR(new char[] { '|' }, true, true);

        // The order of the values follows the binding order of the operators (NOT binds tightest, OR binds loosest)

        private final char[] chars;
        public final boolean requiresLeftArg;
        public final boolean requiresRightArg;

        Operator(char[] chars, boolean requiresLeftArg, boolean requiresRightArg) {
            this.chars = chars;
            this.requiresLeftArg = requiresLeftArg;
            this.requiresRightArg = requiresRightArg;
        }

        public boolean hasChar(char c) {
            return arrContains(chars, c);
        }

        public static boolean isNOT(char c) {
            return arrContains(NOT.chars, c);
        }

        public static boolean isAND(char c) {
            return arrContains(AND.chars, c);
        }

        public static boolean isOR(char c) {
            return arrContains(OR.chars, c);
        }

        public static boolean isOperator(char c) {
            return isNOT(c) || isAND(c) || isOR(c);
        }

        private static boolean arrContains(char[] arr, char c) {
            for(char value : arr) {
                if(value == c) {
                    return true;
                }
            }
            return false;
        }

        private static Operator fromChar(char c) {
            for(Operator op : values()) {
                if(arrContains(op.chars, c)) {
                    return op;
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
