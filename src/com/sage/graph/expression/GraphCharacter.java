package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GraphCharacter {
    private final String character;
    private final CharacterType charType;

    public GraphCharacter(String character) {
        this.character = character;
        charType = Arrays.stream(CharacterType.values())
                .filter(type -> type.clazz == this.getClass())
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Error: could not find a CharacterType value that matched "
                        + "this GraphCharacter object's class. This GraphCharacter's object's class is: "
                        + this.getClass().getName()));
    }

    public GraphCharacter(GraphCharacter other) {
        this.character = other.character;
        this.charType = other.charType;
    }

    public boolean isOperator() {
        return charType == CharacterType.OPERATOR;
    }

    public boolean isVariable() {
        return charType == CharacterType.VARIABLE;
    }

    public boolean isBracket() {
        return charType == CharacterType.BRACKET;
    }

    public boolean isOpenBracket() {
        return this instanceof Bracket b && b.type == Bracket.BracketType.OPEN;
    }

    public boolean isCloseBracket() {
        return this instanceof Bracket b && b.type == Bracket.BracketType.CLOSE;
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

    public enum CharacterType {
        VARIABLE(Variable.getRegex(), Variable::new, Variable.class),
        BRACKET(Bracket.getRegex(), Bracket::new, Bracket.class),
        OPERATOR(Operator.getRegex(), Operator::new, Operator.class);

        private final Pattern regex;
        private final CharacterConstructor constructor;
        private final Class<?> clazz;

        CharacterType(Pattern regex, CharacterConstructor constructor, Class<?> clazz) {
            this.regex = regex;
            this.constructor = constructor;
            this.clazz = clazz;
        }

        public Matcher matcher(String exp) {
            return regex.matcher(exp);
        }

        public GraphCharacter newGraphCharacter(String c) {
            return constructor.newChar(c);
        }

        private interface CharacterConstructor {
            GraphCharacter newChar(String c);
        }
    }
}
