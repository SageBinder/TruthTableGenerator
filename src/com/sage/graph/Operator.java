package com.sage.graph;

import com.sage.nodes.Node;

import java.util.List;

public enum Operator {
    NOT(new char[] { '~', '!' }, false, true,
            (tag, inputs) -> new com.sage.nodes.NOT(tag, inputs[1])),
    AND(new char[] { '&' }, true, true,
            (tag, inputs) -> new com.sage.nodes.AND(tag, inputs[0], inputs[1])),
    OR(new char[] { '|' }, true, true,
            (tag, inputs) -> new com.sage.nodes.OR(tag, inputs[0], inputs[1])),
    IF(new char[] { '>' }, true, true,
            (tag, inputs) -> new com.sage.nodes.IF(tag, inputs[0], inputs[1])),
    IFF(new char[] { '=' }, true, true,
            (tag, inputs) -> new com.sage.nodes.IFF(tag, inputs[0], inputs[1]));

    // The order of the values follows the binding order of the operators (NOT binds tightest)

    private final char[] chars;
    private final Node.NodeConstructor<Node> constructor;
    
    public final boolean requiresLeftArg;
    public final boolean requiresRightArg;
    
    Operator(char[] chars, boolean requiresLeftArg, boolean requiresRightArg, Node.NodeConstructor<Node> constructor) {
        this.chars = chars;
        this.requiresLeftArg = requiresLeftArg;
        this.requiresRightArg = requiresRightArg;
        this.constructor = constructor;
    }

    public boolean hasChar(char c) {
        return arrContains(chars, c);
    }

    public Node newNode(Node leftInput, Node rightInput) {
        return newNode("", leftInput, rightInput);
    }

    public Node newNode(String tag, Node leftInput, Node rightInput) {
        return constructor.newNode(tag, leftInput, rightInput);
    }

    public static boolean isOperatorChar(char c) {
        return List.of(Operator.values()).stream().anyMatch(op -> op.hasChar(c));
    }

    public static Operator fromChar(char c) {
        for(Operator op : values()) {
            if(arrContains(op.chars, c)) {
                return op;
            }
        }

        throw new IllegalArgumentException();
    }

    private static boolean arrContains(char[] arr, char c) {
        for(char value : arr) {
            if(value == c) {
                return true;
            }
        }
        return false;
    }
}
