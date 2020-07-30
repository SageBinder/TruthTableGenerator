package com.sage.token;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.base.Node;
import com.sage.token.type.*;
import com.sage.token.type.base.NodeTokenType;
import com.sage.token.type.base.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public sealed abstract class Token<T extends TokenType> permits Token.NodeToken, Token.NonNodeToken {
    public static final List<TokenType> TOKEN_TYPES = new ArrayList<>();
    static {
        // The order here determines parse order. VERY IMPORTANT! DO NOT CHANGE ORDER UNLESS YOU KNOW WHAT YOU ARE DOING!
        TOKEN_TYPES.addAll(List.of(Constant.values()));
        TOKEN_TYPES.addAll(List.of(Quantifier.values()));
        TOKEN_TYPES.addAll(List.of(Variable.values()));
        TOKEN_TYPES.addAll(List.of(BooleanOperator.values()));
        TOKEN_TYPES.addAll(List.of(Bracket.values()));
    }

    public final String tokenString;

    public final ParseMode parseMode;

    public final T tokenType;

    private Token(String tokenString, T tokenType, ParseMode parseMode) {
        this.tokenString = tokenString;
        this.tokenType = tokenType;
        this.parseMode = parseMode;
    }

    public static Token<?> newToken(String tokenString, ParseMode parseMode) {
        var tokenType = TOKEN_TYPES.stream()
                .filter(type -> type.matches(tokenString, parseMode))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidInputException(
                                "Error: could not match token \"" + tokenString + "\" to any token type")
                );
        if(tokenType instanceof NodeTokenType nodeTokenType) {
            return new NodeToken(tokenString, nodeTokenType, parseMode);
        } else {
            return new NonNodeToken(tokenString, tokenType, parseMode);
        }
    }

    public abstract boolean isNodeToken();

    public boolean isBracket() {
        return tokenType instanceof Bracket;
    }

    public boolean isOpenBracket() {
        return tokenType == Bracket.OPEN_BRACKET;
    }

    public boolean isCloseBracket() {
        return tokenType == Bracket.CLOSE_BRACKET;
    }

    @Override
    public String toString() {
        return tokenString;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Token<?> that = (Token<?>)o;

        return Objects.equals(tokenString, that.tokenString);
    }

    @Override
    public int hashCode() {
        return tokenString != null ? tokenString.hashCode() : 0;
    }

    public static Matcher combinedMatcher(String input, ParseMode parseMode) {
        return TokenUtils.combinePatterns(TOKEN_TYPES.toArray(TokenType[]::new), parseMode).matcher(input);
    }

    public static non-sealed class NodeToken extends Token<NodeTokenType> {
        private NodeToken(String tokenString, NodeTokenType tokenType, ParseMode parseMode) {
            super(tokenString, tokenType, parseMode);
        }

        @Override
        public boolean isNodeToken() {
            return true;
        }

        public Node makeNode(String tag, Node... parents) {
            return tokenType.makeNode(tag, tokenString, parseMode, parents);
        }
    }

    public static non-sealed class NonNodeToken extends Token<TokenType> {
        private NonNodeToken(String tokenString, TokenType tokenType, ParseMode parseMode) {
            super(tokenString, tokenType, parseMode);
        }

        @Override
        public boolean isNodeToken() {
            return false;
        }
    }
}

