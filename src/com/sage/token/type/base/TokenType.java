package com.sage.token.type.base;

import com.sage.token.ParseMode;

import java.util.regex.Pattern;

public interface TokenType {
    Pattern getPattern(ParseMode parseMode);

    default boolean matches(String input, ParseMode parseMode) {
        return getPattern(parseMode).matcher(input).matches();
    }
}
