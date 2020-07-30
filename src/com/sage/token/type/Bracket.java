package com.sage.token.type;

import com.sage.token.ParseMode;
import com.sage.token.type.base.TokenType;

import java.util.regex.Pattern;

public enum Bracket implements TokenType {
    OPEN_BRACKET("(") {
        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[\\[{(]");
        }
    },

    CLOSE_BRACKET(")") {
        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile("[]})]");
        }
    };

    public final String DEFAULT_SEQUENCE;

    Bracket(String DEFAULT_SEQUENCE) {
        this.DEFAULT_SEQUENCE = DEFAULT_SEQUENCE;
    }
}
