package com.sage.token.tokens;

import com.sage.token.parsing.ParseMode;
import com.sage.token.type.base.TokenType;

public non-sealed class NonNodeToken extends Token<TokenType> {
    protected NonNodeToken(String tokenString, TokenType tokenType, ParseMode parseMode) {
        super(tokenString, tokenType, parseMode);
    }

    @Override
    public boolean isNodeToken() {
        return false;
    }
}
