package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Disallow non-identical brackets to match (i.e something like "(A & B]")

public class GraphString {
    private final List<GraphCharacter> string;

    public GraphString(String rawExpression) {
        string = stringToGraphString(cleanRawExpression(rawExpression));
        cleanGraphExpression();
    }

    private <T extends GraphCharacter> GraphString(List<T> string) {
        this.string = new ArrayList<>(string);
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

        if(!charAt(operatorIdx).isOperator()) {
            throwError("getLeftOperandIndices(int idx) received and idx of a character which is not an operator");
        }

        int parenDepth = 0;
        for(int i = operatorIdx - 1; i >= 0; i--) {
            if(this.charAt(i).isOpenBracket()) {
                parenDepth++;
            } else if(this.charAt(i).isCloseBracket()) {
                parenDepth--;
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

        if(!charAt(operatorIdx).isOperator()) {
            throwError("getRightOperandIndices(int idx) received and idx of a character which is not an operator");
        }

        int parenDepth = 0;
        for(int i = operatorIdx + 1; i < this.length(); i++) {
            if(this.charAt(i).isOpenBracket()) {
                parenDepth++;
            } else if(this.charAt(i).isCloseBracket()) {
                parenDepth--;
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
            if(c.isOpenBracket()) {
                parenDepth++;
            } else if(c.isCloseBracket()) {
                parenDepth--;
            }

            if(parenDepth < 0) {
                return false;
            }
        }

        return parenDepth == 0;
    }

    // Is allowed mutate string (only called from constructor)
    private void addImpliedParens() {
        for(var op : Operator.OpType.values()) {
            for(int i = string.size() - 1; i >= 0; i--) {
                if(charAt(i) instanceof Operator opChar && opChar.opType == op && !operatorHasParens(i)) {
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
        if(charAt(operatorIdx) instanceof Operator opChar) {
            int initialLength = length();

            // The right parenthesis must be inserted first, so that operatorIdx does not shift.

            // Right parenthesis:
            if(opChar.requiresRightArg()) {
                for(int i = operatorIdx + 1, parenDepth = 0; i < initialLength; i++) {
                    if(this.charAt(i).isOpenBracket()) {
                        parenDepth++;
                    } else if(this.charAt(i).isCloseBracket()) {
                        parenDepth--;
                    }

                    if(parenDepth == 0) {
                        string.add(i + 1, Bracket.newCloseBracket());
                        break;
                    }
                }
            } else {
                string.add(operatorIdx + 1, Bracket.newCloseBracket());
            }

            // Left parenthesis
            if(opChar.requiresLeftArg()) {
                for(int i = operatorIdx - 1, parenDepth = 0; i >= 0; i--) {
                    if(this.charAt(i).isOpenBracket()) {
                        parenDepth++;
                    } else if(this.charAt(i).isCloseBracket()) {
                        parenDepth--;
                    }

                    if(parenDepth == 0) {
                        string.add(i, Bracket.newOpenBracket());
                        break;
                    }
                }
            } else {
                string.add(operatorIdx, Bracket.newOpenBracket());
            }
        } else {
            throwError("addParensAroundOperator(int operatorIdx) received the idx of a non-operator GraphCharacter. operatorIdx = "
                    + operatorIdx
                    + ", char = '"
                    + charAt(operatorIdx)
                    + "'");
        }
    }

    private boolean operatorHasParens(int operatorIdx) {
        if(charAt(operatorIdx) instanceof Operator opChar) {
            if(opChar.requiresRightArg()) {
                int[] rightOperandIndices = getRightOperandIndices(operatorIdx);
                if(!(rightOperandIndices[1] < length() - 1 && charAt(rightOperandIndices[1] + 1).isCloseBracket())) {
                    return false;
                }
            } else if(!(operatorIdx < length() - 1 && charAt(operatorIdx + 1).isCloseBracket())) {
                return false;
            }

            if(opChar.requiresLeftArg()) {
                int[] leftOperandIndices = getLeftOperandIndices(operatorIdx);
                if(!(leftOperandIndices[0] > 0 && charAt(leftOperandIndices[0] - 1).isOpenBracket())) {
                    return false;
                }
            } else if(!(operatorIdx > 0 && charAt(operatorIdx - 1).isOpenBracket())) {
                return false;
            }

            return true;
        } else {
            throw generateError("operatorHasParens(int operatorIdx) received the idx of a non-operator GraphCharacter. operatorIdx = "
                    + operatorIdx
                    + ", char = '"
                    + charAt(operatorIdx)
                    + "'");
        }
    }

    public InvalidInputException generateError(String message) {
        return new InvalidInputException(message + "\nInvalid expression: \"" + toString() + "\"");
    }

    public void throwError(String message) throws InvalidInputException {
        throw generateError(message);
    }

    public int indexOfTopLevelOperator() {
        int minOperatorDepth = length();
        int minOperatorDepthIdx = -1;
        int currDepth = 0;

        for(int i = 0; i < length(); i++) {
            var c = charAt(i);

            if(c.isOperator() && currDepth < minOperatorDepth) {
                minOperatorDepth = currDepth;
                minOperatorDepthIdx = i;
            } else if(c.isOpenBracket()) {
                currDepth++;
            } else if(c.isCloseBracket()) {
                currDepth--;
            }
        }

        return minOperatorDepthIdx;
    }

    public long numVariables() {
        return string.stream().filter(GraphCharacter::isVariable).count();
    }

    public Optional<Variable> findFirstVariable() {
        return string.stream().filter(GraphCharacter::isVariable).map(c -> (Variable)c).findFirst();
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

        GraphCharacter[] string = new GraphCharacter[exp.length()];
        Matcher matcher;

        // We must keep track of which characters have already been interpreted, so that characters will not be double
        // interpreted by two or more GraphCharacters
        Set<Integer> expIndicesAlreadyUsed = new HashSet<>();

        for(var charType : GraphCharacter.CharacterType.values()) {
            matcher = charType.matcher(exp);
            while(matcher.find()) {
                Set<Integer> matchedRange =
                        IntStream.range(matcher.start(), matcher.end())
                                .boxed()
                                .collect(Collectors.toSet());


                // Calculate the intersection between the current matched range and the previous matched ranges
                Set<Integer> rangeIntersection = new HashSet<>(matchedRange);
                rangeIntersection.retainAll(expIndicesAlreadyUsed);

                // If there's no intersection, none of the chars are being doubly interpreted, so we can go ahead and
                // interpret the regex match
                if(rangeIntersection.size() == 0) {
                    string[matcher.start()] = charType.newGraphCharacter(matcher.group());

                    // Add the matched range to the set of previously matched ranges.
                    expIndicesAlreadyUsed.addAll(matchedRange);
                }

                if(matcher.hitEnd()) {
                    break;
                }
            }
        }

        return Arrays.stream(string)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
