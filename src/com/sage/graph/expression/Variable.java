package com.sage.graph.expression;

import java.util.regex.Pattern;

public class Variable extends GraphCharacter {
    private static final Pattern pattern = Pattern.compile("[a-zA-Z1-9]+");

    public Variable(String character) {
        super(character);
    }

    public Variable(GraphCharacter other) {
        super(other);
    }

    public static Pattern getRegex() {
        return pattern;
    }
}
