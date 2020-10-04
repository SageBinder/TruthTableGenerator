package com.sage.token.type;

import com.sage.nodes.quantifiers.EXISTENTIAL_QUANTIFIER;
import com.sage.nodes.quantifiers.UNIVERSAL_QUANTIFIER;
import com.sage.nodes.base.Node;
import com.sage.token.tokens.Token;
import com.sage.token.type.base.NodeTokenType;
import com.sage.token.parsing.ParseMode;
import com.sage.token.parsing.TokenArgIndices;
import com.sage.token.parsing.TokenArgs;
import com.sage.token.parsing.TokenList;

import java.util.List;
import java.util.regex.Pattern;

import static com.sage.token.parsing.TokenUtils.MATCH_NOTHING;

public enum Quantifier implements NodeTokenType {
    UNIVERSAL_QUANTIFIER {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            String[] bindingVariables = tokenString.replaceAll("[A∀()\\s]", "").split(",");

            var lastParent = parents[0];
            for(var bindingVariable : bindingVariables) {
                lastParent = new UNIVERSAL_QUANTIFIER(tag, bindingVariable, lastParent);
            }
            return lastParent;
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            var pattern = String.format("\\([A∀]?(?:%s(?:,)?)+\\)",
                    Variable.VARIABLE_NAME_PATTERN);
            return parseMode == ParseMode.PL ? Pattern.compile(pattern) : MATCH_NOTHING;
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return new TokenArgs(new TokenArgIndices(0, 0), new TokenList[0]); //TODO
        }
    },

    EXISTENTIAL_QUANTIFIER {
        @Override
        public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
            String[] bindingVariables = tokenString.replaceAll("[E∃()\\s]", "").split(",");

            var lastParent = parents[0];
            for(var bindingVariable : bindingVariables) {
                lastParent = new EXISTENTIAL_QUANTIFIER(tag, bindingVariable, lastParent);
            }
            return lastParent;
        }

        @Override
        public Pattern getPattern(ParseMode parseMode) {
            var pattern = String.format("\\([E∃](?:%s(?:,)?)+\\)",
                    Variable.VARIABLE_NAME_PATTERN);
            return parseMode == ParseMode.PL ? Pattern.compile(pattern) : MATCH_NOTHING;
        }

        @Override
        public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode) {
            return new TokenArgs(new TokenArgIndices(0, 0), new TokenList[0]); //TODO
        }
    },
}
