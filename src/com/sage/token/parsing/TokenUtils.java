package com.sage.token.parsing;

import com.sage.exceptions.InvalidInputException;
import com.sage.token.tokens.Token;
import com.sage.token.type.base.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TokenUtils {
    public static final Pattern MATCH_NOTHING = Pattern.compile("a^");

    public static Pattern combinePatterns(TokenType[] types, ParseMode parseMode) {
        return Pattern.compile(
                Arrays.stream(types)
                        .map(type -> type.getPattern(parseMode).toString())
                        .collect(Collectors.joining("|")));
    }

    public static int[] getLeftArgsIndices(List<Token<?>> tokenList, int operatorIdx) {
        if(operatorIdx >= tokenList.size() || operatorIdx < 0) {
            throw new InvalidInputException("getLeftArgIndices(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == 0) {
            throw new InvalidInputException("getLeftArgIndices(int idx) received an idx of 0. This is bad.");
        }

        int parenDepth = 0;
        for(int i = operatorIdx - 1; i >= 0; i--) {
            var c = tokenList.get(i);
            if(c.isOpenBracket()) {
                parenDepth++;
            } else if(c.isCloseBracket()) {
                parenDepth--;
            }

            if(parenDepth == 0) {
                return new int[] { i, operatorIdx - 1 };
            }
        }

        return new int[] { 0, operatorIdx - 1 };
    }

    public static int[] getRightArgsIndices(List<Token<?>> tokenList, int operatorIdx) {
        if(operatorIdx >= tokenList.size() || operatorIdx < 0) {
            throw new InvalidInputException("getRightArgIndices(int idx) received an out-of-bounds index. This is bad.");
        }

        if(operatorIdx == tokenList.size() - 1) {
            throw new InvalidInputException("getRightArgIndices(int idx) received an idx of (exp.length() - 1)). This is bad.");
        }

        int parenDepth = 0;
        for(int i = operatorIdx + 1; i < tokenList.size(); i++) {
            var c = tokenList.get(i);
            if(c.isOpenBracket()) {
                parenDepth++;
            } else if(c.isCloseBracket()) {
                parenDepth--;
            }

            if(parenDepth == 0) {
                return new int[] { operatorIdx + 1, i };
            }
        }

        return new int[] { operatorIdx + 1, tokenList.size() - 1 };
    }
}
