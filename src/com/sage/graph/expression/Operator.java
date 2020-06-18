package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.Node;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Operator extends NodeGraphCharacter {
    public final OpType opType;

    public Operator(String character) {
        super(character);

        try {
            this.opType = OpType.fromString(toString());
        } catch(IllegalArgumentException e) {
            throw new InvalidInputException("Error: could not construct an operator from the string \""
                    + toString()
                    + "\"");
        }
    }

    public Operator(GraphCharacter other) {
        super(other);

        if(other instanceof Operator otherOperator) {
            this.opType = otherOperator.opType;
        } else try {
            this.opType = OpType.fromString(toString());
        } catch(IllegalArgumentException e) {
            throw new InvalidInputException("Error: could not construct an operator from the string \""
                    + toString()
                    + "\"");
        }
    }

    // If this node does not require an argument, it is ok to pass null for that argument.
    // i.e, if requireLeftArg() returns false, then the left input node for this method can be null (it will be ignored).
    @Override
    public Node makeNode(String tag, Node left, Node right) {
        return opType.constructor.newNode(tag, left, right);
    }

    @Override
    public boolean requiresLeftArg() {
        return opType.requiresLeftArg;
    }

    @Override
    public boolean requiresRightArg() {
        return opType.requiresRightArg;
    }

    public static Pattern getRegex() {
        return Pattern.compile(
                Arrays.stream(OpType.values())
                        .map(op -> op.pattern.toString())
                        .collect(Collectors.joining("|")));
    }

    public enum OpType {
        // The order of the values MUST follow the binding order of the operators (NOT binds tightest)
        NOT(Pattern.compile("[~!]"),
                false,
                true,
                (tag, inputs) -> new com.sage.nodes.NOT(tag, inputs[1])),
        AND(Pattern.compile("[&]{1,2}"),
                true,
                true,
                (tag, inputs) -> new com.sage.nodes.AND(tag, inputs[0], inputs[1])),
        OR(Pattern.compile("\\+|[|]{1,2}"),
                true,
                true,
                (tag, inputs) -> new com.sage.nodes.OR(tag, inputs[0], inputs[1])),
        IF(Pattern.compile("[>]"),
                true,
                true,
                (tag, inputs) -> new com.sage.nodes.IF(tag, inputs[0], inputs[1])),
        IFF(Pattern.compile("[=]{1,2}"),
                true,
                true,
                (tag, inputs) -> new com.sage.nodes.IFF(tag, inputs[0], inputs[1]));

        private final Pattern pattern;
        private final NodeConstructor constructor;

        private final boolean requiresLeftArg;
        private final boolean requiresRightArg;

        OpType(Pattern pattern, boolean requiresLeftArg, boolean requiresRightArg, NodeConstructor constructor) {
            this.pattern = pattern;
            this.requiresLeftArg = requiresLeftArg;
            this.requiresRightArg = requiresRightArg;
            this.constructor = constructor;
        }

        private static OpType fromString(String opChar) {
            for(var op : values()) {
                if(op.pattern.matcher(opChar).matches()) {
                    return op;
                }
            }

            throw new IllegalArgumentException();
        }

        private interface NodeConstructor {
            default Node newNode(Node... inputs) {
                return newNode("", inputs);
            }

            Node newNode(String tag, Node... inputs);
        }
    }
}

