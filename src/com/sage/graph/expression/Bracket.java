package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Bracket extends GraphCharacter {
    public final BracketType type;

    public Bracket(String character) {
        super(character);
        try {
            this.type = BracketType.fromString(toString());
        } catch(IllegalArgumentException e) {
            throw new InvalidInputException("Error: could not construct a bracket from the string \""
                    + toString()
                    + "\"");
        }
    }

    public Bracket(GraphCharacter other) {
        super(other);
        if(other instanceof Bracket otherBracket) {
            this.type = otherBracket.type;
        } else try {
            this.type = BracketType.fromString(toString());
        } catch(IllegalArgumentException e) {
            throw new InvalidInputException("Error: could not construct a bracket from the string \""
                    + toString()
                    + "\"");
        }
    }

    public static Bracket newOpenBracket() {
        return new Bracket(BracketType.OPEN.defaultSequence);
    }

    public static Bracket newCloseBracket() {
        return new Bracket(BracketType.CLOSE.defaultSequence);
    }

    public static Pattern getRegex() {
        return Pattern.compile(
                Arrays.stream(BracketType.values())
                        .map(bracketType -> bracketType.pattern.toString())
                        .collect(Collectors.joining("|")));
    }

    public enum BracketType {
        OPEN(Pattern.compile("[\\[{(]"), "("),
        CLOSE(Pattern.compile("[]})]"), ")");

        private final Pattern pattern;
        private final String defaultSequence;

        BracketType(Pattern pattern, String defaultSequence) {
            this.pattern = pattern;
            this.defaultSequence = defaultSequence;
        }

        public boolean matches(String bracketChar) {
            return pattern.matcher(bracketChar).matches();
        }

        public static BracketType fromString(String bracketChar) {
            for(var type : values()) {
                if(type.matches(bracketChar)) {
                    return type;
                }
            }

            throw new IllegalArgumentException();
        }
    }
}
