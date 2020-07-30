package com.sage.token.type;

import com.sage.nodes.constants.TAUTOLOGY;
import com.sage.nodes.constants.CONTRADICTION;
import com.sage.nodes.base.Node;
import com.sage.token.ParseMode;
import com.sage.token.type.base.NoArgsNodeTokenType;

import java.util.regex.Pattern;

public enum Constant implements NoArgsNodeTokenType {
    TAUTOLOGY {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new TAUTOLOGY();
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[1⊤]");
        }
    },

    CONTRADICTION {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            return new CONTRADICTION();
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[0⊥]");
        }
    },
}
