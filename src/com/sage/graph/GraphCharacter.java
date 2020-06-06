package com.sage.graph;

import java.util.Arrays;
import java.util.Objects;

public class GraphCharacter {
    public static final String[] OPEN_BRACKETS = {
            "(",
            "{",
            "[",
    };

    public static final String[] CLOSE_BRACKETS = {
            ")",
            "}",
            "]",
    };

    private final String character;

    public GraphCharacter(String character) {
        this.character = character;
    }

    public GraphCharacter(GraphCharacter other) {
        this.character = other.character;
    }

    public boolean isOperator() {
        return Operator.isOperatorChar(this);
    }

    public boolean isOpenBracket() {
        return Arrays.asList(OPEN_BRACKETS).contains(character);
    }

    public boolean isCloseBracket() {
        return Arrays.asList(CLOSE_BRACKETS).contains(character);
    }

    public boolean isVariable() {
        return character.chars().mapToObj(c -> (char)c).allMatch(GraphCharacter::isVariableChar);
    }

    public static boolean isVariableChar(char c) {
        return Character.isAlphabetic(c) | Character.isDigit(c);
    }

    @Override
    public String toString() {
        return character;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        GraphCharacter that = (GraphCharacter)o;

        return Objects.equals(character, that.character);
    }

    @Override
    public int hashCode() {
        return character != null ? character.hashCode() : 0;
    }
}
