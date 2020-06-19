package com.sage.graph.expression;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.Node;
import com.sage.nodes.VARIABLE;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Operator extends NodeGraphCharacter {
    public final OpType opType;
    public final ParseMode parseMode;

    public Operator(String character, ParseMode parseMode) {
        super(character);

        this.parseMode = parseMode;
        try {
            this.opType = OpType.fromString(toString(), parseMode);
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
            this.parseMode = otherOperator.parseMode;
        } else throw new InvalidInputException("Error: Operator copy constructor received a non-Operator argument");
    }

    // If this node does not require an argument, it is ok to pass null for that argument.
    // i.e, if requireLeftArg() returns false, then the left input node for this method can be null (it will be ignored).
    @Override
    public Node makeNode(String tag, Node left, Node right) {
        return opType.makeNode(tag, left, right);
    }

    @Override
    public boolean requiresLeftArg() {
        return opType.requiresLeftArg;
    }

    @Override
    public boolean requiresRightArg() {
        return opType.requiresRightArg;
    }

    public static Pattern getRegex(ParseMode parseMode) {
        return Pattern.compile(
                Arrays.stream(OpType.values())
                        .map(op -> op.getPattern(parseMode).toString())
                        .collect(Collectors.joining("|")));
    }

    public enum OpType {
        // The order of the values MUST follow the binding order of the operators (quantifiers binds tightest)
        EXISTENTIAL_QUANTIFIER(false, true) {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.EXISTENTIAL_QUANTIFIER(tag, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("\\(E[w-z]\\)") : MATCH_NOTHING_REGEX;
            }
        },

        UNIVERSAL_QUANTIFIER(false, true) {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.UNIVERSAL_QUANTIFIER(tag, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("\\([w-z]\\)") : MATCH_NOTHING_REGEX;
            }
        },

        // The variable values won't affect binding order, because they don't bind to anything.
        // However, QUANTIFIED_VARIABLE must come before VARIABLE, because a quantified variable will match both regexes.
        QUANTIFIED_VARIABLE(false, false) {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.QUANTIFIED_VARIABLE(tag);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return parseMode == ParseMode.PL ? Pattern.compile("[A-T]([a-r]|[w-z])+") : MATCH_NOTHING_REGEX;
            }
        },

        VARIABLE(false, false) {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                // Brackets in the variable's tag name are removed
                return new VARIABLE(tag.replaceAll(Bracket.getRegex().toString(), ""));
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[a-zA-Z1-9]+");
            }
        },

        NOT(false, true) {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.NOT(tag, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[~!]");
            }
        },

        AND() {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.AND(tag, left, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[&]{1,2}");
            }
        },

        OR() {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.OR(tag, left, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("\\+|[|]{1,2}");
            }
        },

        IF() {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.IF(tag, left, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[>]");
            }
        },

        IFF() {
            @Override
            Node makeNode(String tag, Node left, Node right) {
                return new com.sage.nodes.IFF(tag, left, right);
            }

            @Override
            Pattern getPattern(ParseMode parseMode) {
                return Pattern.compile("[=]{1,2}");
            }
        };

        private static final Pattern MATCH_NOTHING_REGEX = Pattern.compile("a^");

        private final boolean requiresLeftArg;
        private final boolean requiresRightArg;

        OpType() {
            this(true, true);
        }

        OpType(boolean requiresLeftArg, boolean requiresRightArg) {
            this.requiresLeftArg = requiresLeftArg;
            this.requiresRightArg = requiresRightArg;
        }

        private static OpType fromString(String opChar, ParseMode parseMode) {
            for(var op : values()) {
                if(op.getPattern(parseMode).matcher(opChar).matches()) {
                    return op;
                }
            }

            throw new IllegalArgumentException();
        }

        abstract Node makeNode(String tag, Node left, Node right);

        abstract Pattern getPattern(ParseMode parseMode);

        private interface NodeConstructor {
            default Node newNode(Node... inputs) {
                return newNode("", inputs);
            }

            Node newNode(String tag, Node... inputs);
        }
    }
}

