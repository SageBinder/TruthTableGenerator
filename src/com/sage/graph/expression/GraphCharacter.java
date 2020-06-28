package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.VARIABLE;
import com.sage.nodes.base.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GraphCharacter {
    public static final String DEFAULT_OPEN_BRACKET_SEQUENCE = "(";
    public static final String DEFAULT_CLOSE_BRACKET_SEQUENCE = ")";

    private final String character;

    public final GraphCharacterType charType;

    public GraphCharacter(String character, ParseMode parseMode) {
        this.character = character;
        try {
            this.charType = GraphCharacterType.fromString(toString(), parseMode);
        } catch(IllegalArgumentException e) {
            throw new InvalidInputException("Error: could not construct an operator from the string \""
                    + toString()
                    + "\"");
        }
    }

    // If this node does not require an argument, it is ok to pass null for that argument.
    // i.e, if requireLeftArg() returns false, then the left input node for this method can be null (it will be ignored).
    public Node makeNode(String tag, Node left, Node right, ParseMode parseMode) {
        return charType.makeNode(tag, character, left, right, parseMode);
    }

    public boolean isOperator() {
        // TODO: Find a better, more programmatic way to do this (I want to try to avoid treating brackets as special cases)
        return charType != GraphCharacterType.OPEN_BRACKET && charType != GraphCharacterType.CLOSE_BRACKET;
    }

    public boolean requiresLeftArg() {
        return charType.requiresLeftArg;
    }

    public boolean requiresRightArg() {
        return charType.requiresRightArg;
    }

    public boolean isBracket() {
        return !isOpenBracket() && !isCloseBracket();
    }

    public boolean isOpenBracket() {
        return this.charType == GraphCharacterType.OPEN_BRACKET;
    }

    public boolean isCloseBracket() {
        return this.charType == GraphCharacterType.CLOSE_BRACKET;
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

    public static GraphCharacter newOpenBracket() {
        return new GraphCharacter(DEFAULT_OPEN_BRACKET_SEQUENCE, null);
    }

    public static GraphCharacter newCloseBracket() {
        return new GraphCharacter(DEFAULT_CLOSE_BRACKET_SEQUENCE, null);
    }

    public static Matcher getNewCombinedMatcher(String exp, ParseMode parseMode) {
        return combinePatterns(GraphCharacterType.values(), parseMode).matcher(exp);
    }

    private static Pattern combinePatterns(GraphCharacterType[] types, ParseMode parseMode) {
        return Pattern.compile(
                Arrays.stream(types)
                        .map(type -> type.getPattern(parseMode).toString())
                        .collect(Collectors.joining("|")));
    }

    public enum GraphCharacterType {
        // The order of the values MUST follow the binding order of the operators (quantifiers binds tightest)
        EXISTENTIAL_QUANTIFIER(false, true) {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                String bindingVariable = String.valueOf(character.charAt(2)); // LAZY
                return new com.sage.nodes.EXISTENTIAL_QUANTIFIER(tag, bindingVariable, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("\\(E[w-z]\\)") : MATCH_NOTHING;
            }
        },

        UNIVERSAL_QUANTIFIER(false, true) {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                String bindingVariable = String.valueOf(character.charAt(1)); // LAZY
                return new com.sage.nodes.UNIVERSAL_QUANTIFIER(tag, bindingVariable, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("\\([w-z]\\)") : MATCH_NOTHING;
            }
        },

        // The variable values won't affect binding order, because they don't bind to anything.
        // However, QUANTIFIED_VARIABLE must come before VARIABLE, because a quantified variable will match both regexes.
        QUANTIFIED_VARIABLE(false, false) {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                List<String> variables = List.of(character.substring(1).split(""));
                String variableName = String.valueOf(character.charAt(0));
                return new com.sage.nodes.QUANTIFIED_VARIABLE(tag, variableName, variables);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("[A-T]([a-r]|[w-z])+") : MATCH_NOTHING;
            }
        },

        VARIABLE(false, false) {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                // Brackets in the variable's tag name are removed
                return new VARIABLE(
                        tag.replaceAll(
                                combinePatterns(new GraphCharacterType[] { OPEN_BRACKET, CLOSE_BRACKET }, parseMode).toString(),
                                ""));
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[a-zA-Z1-9]+");
            }
        },

        NOT(false, true) {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                return new com.sage.nodes.NOT(tag, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[~!]");
            }
        },

        AND() {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                return new com.sage.nodes.AND(tag, left, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[&]{1,2}");
            }
        },

        OR() {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                return new com.sage.nodes.OR(tag, left, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("\\+|[|]{1,2}");
            }
        },

        IF() {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                return new com.sage.nodes.IF(tag, left, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[>]");
            }
        },

        IFF() {
            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                return new com.sage.nodes.IFF(tag, left, right);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[=]{1,2}");
            }
        },

        OPEN_BRACKET(false, false) {
            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[\\[{(]");
            }

            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                throw new UnsupportedOperationException("Error: cannot create node from open bracket");
            }
        },

        CLOSE_BRACKET(false, false) {
            @Override
            public Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[]})]");
            }

            @Override
            Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode) {
                throw new UnsupportedOperationException("Error: cannot create node from close bracket");
            }
        };

        private static final Pattern MATCH_NOTHING = Pattern.compile("a^");

        public final boolean requiresLeftArg;
        public final boolean requiresRightArg;

        GraphCharacterType() {
            this(true, true);
        }

        GraphCharacterType(boolean requiresLeftArg, boolean requiresRightArg) {
            this.requiresLeftArg = requiresLeftArg;
            this.requiresRightArg = requiresRightArg;
        }

        private static GraphCharacterType fromString(String opChar, ParseMode parseMode) {
            for(var op : values()) {
                if(op.getPattern(parseMode).matcher(opChar).matches()) {
                    return op;
                }
            }

            throw new IllegalArgumentException();
        }

        abstract Node makeNode(String tag, String character, Node left, Node right, ParseMode parseMode);

        abstract Pattern getPattern(ParseMode parseMode);
    }
}

