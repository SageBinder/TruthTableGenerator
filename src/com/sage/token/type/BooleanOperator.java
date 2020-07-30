package com.sage.token.type;

import com.sage.nodes.base.Node;
import com.sage.nodes.booleanoperators.*;
import com.sage.token.*;
import com.sage.token.type.base.NodeTokenType;

import java.util.List;
import java.util.regex.Pattern;

public enum BooleanOperator implements NodeTokenType {
    NOT(false, true) {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new NOT(tag, parents[0]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[~˜!¬]");
        }
    },

    AND {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new AND(tag, parents[0], parents[1]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[&∧]{1,2}");
        }
    },

    NAND {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new NAND(tag, parents[0], parents[1]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[.]");
        }
    },

    OR(true, true) {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new OR(tag, parents[0], parents[1]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[+∨∥]|[|]{1,2}");
        }
    },

    IF {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new IF(tag, parents[0], parents[1]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[>⇒→⊃]");
        }
    },

    IFF {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new IFF(tag, parents[0], parents[1]);
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return _getArguments(tokenList, idx, parseMode);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[⇔≡↔]|[=]{1,2}]");
        }
    };

    private final boolean requiresLeftArg;
    private final boolean requiresRightArg;

    BooleanOperator() {
        this.requiresLeftArg = true;
        this.requiresRightArg = true;
    }

    BooleanOperator(boolean requiresLeftArg, boolean requiresRightArg) {
        this.requiresLeftArg = requiresLeftArg;
        this.requiresRightArg = requiresRightArg;
    }

    TokenArgs _getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
        var argTokenLists = new TokenList[(requiresLeftArg ? 1 : 0) + (requiresRightArg ? 1 : 0)];
        int relativeLeftIdx = 0;
        int relativeRightIdx = 0;

        if(requiresLeftArg) {
            int[] leftArgIndices = TokenUtils.getLeftArgsIndices(tokenList, idx);
            argTokenLists[0] = new TokenList(
                    tokenList.subList(leftArgIndices[0], leftArgIndices[1] + 1),
                    parseMode);
            relativeLeftIdx = leftArgIndices[1] - leftArgIndices[0];
        }
        if(requiresRightArg) {
            int[] rightArgIndices = TokenUtils.getRightArgsIndices(tokenList, idx);
            argTokenLists[1] = new TokenList(
                    tokenList.subList(rightArgIndices[0], rightArgIndices[1] + 1),
                    parseMode);
            relativeRightIdx = rightArgIndices[1] - rightArgIndices[0];
        }
        
        return new TokenArgs(new TokenArgIndices(relativeLeftIdx, relativeRightIdx), argTokenLists);
    }
}
