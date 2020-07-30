package com.sage.token.type;

import com.sage.nodes.variables.VARIABLE;
import com.sage.nodes.base.Node;
import com.sage.nodes.variables.QUANTIFIED_VARIABLE;
import com.sage.token.ParseMode;
import com.sage.token.type.base.NoArgsNodeTokenType;
import com.sage.token.type.base.TokenType;

import java.util.List;
import java.util.regex.Pattern;

import static com.sage.token.TokenUtils.MATCH_NOTHING;
import static com.sage.token.TokenUtils.combinePatterns;

public enum Variable implements NoArgsNodeTokenType {
    VARIABLE {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            // Brackets in the tag are removed
            return new VARIABLE(
                    tag.replaceAll(
                            combinePatterns(new TokenType[] { Bracket.OPEN_BRACKET, Bracket.CLOSE_BRACKET }, parseMode).toString(),
                            ""));
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            return Pattern.compile(VARIABLE_NAME_PATTERN);
        }
    },

    PREDICATE {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            List<String> variables = List.of(tokenString.substring(1).split("")); // TODO: what even is this retarded shit
            String variableName = String.valueOf(tokenString.charAt(0));
            return new QUANTIFIED_VARIABLE(tag, variableName, variables);
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            var pattern = String.format("%s\\((?:%s(?:,)?)*\\)",
                    VARIABLE_NAME_PATTERN,
                    VARIABLE_NAME_PATTERN);
            return parseMode == ParseMode.PL ? Pattern.compile(pattern) : MATCH_NOTHING;
        }
    };

    public static final String VARIABLE_NAME_PATTERN = "[a-zA-Z_]+[a-zA-Z0-9_]*";
}
