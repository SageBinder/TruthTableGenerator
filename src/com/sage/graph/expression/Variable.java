package com.sage.graph.expression;

import com.sage.nodes.INPUT;
import com.sage.nodes.Node;

import java.util.regex.Pattern;

public class Variable extends NodeGraphCharacter {
    private static final Pattern pattern = Pattern.compile("[a-zA-Z1-9]+");

    public Variable(String character) {
        super(character);
    }

    public Variable(GraphCharacter other) {
        super(other);
    }

    @Override
    public Node makeNode(String tag, Node left, Node right) {
        return new INPUT(tag);
    }

    @Override
    public boolean requiresLeftArg() {
        return false; // Variable requires no arguments
    }

    @Override
    public boolean requiresRightArg() {
        return false; // Variable requires no arguments
    }

    public static Pattern getRegex() {
        return pattern;
    }
}
