package com.sage.token.parsing;

import com.sage.exceptions.InvalidInputException;
import com.sage.token.tokens.Token;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Disallow non-identical brackets to match (i.e something like "(A & B]")

public class TokenList {
    private final List<Token<?>> tokenList;
    public final ParseMode parseMode;

    public TokenList(String rawExpression, ParseMode parseMode) {
        this.parseMode = parseMode;
        tokenList = tokenize(cleanRawExpression(rawExpression), parseMode);
        if(!parensAreBalanced()) {
            throw new InvalidInputException("Error during preprocessing for expression \"" + toString()
                    + "\": parentheses were unbalanced.");
        }
    }

    public TokenList(List<Token<?>> tokenList, ParseMode parseMode) {
        this.tokenList = new ArrayList<>(tokenList);
        this.parseMode = parseMode;
    }

    public Token<?> charAt(int i) {
        return tokenList.get(i);
    }

    public int length() {
        return tokenList.size();
    }

    private boolean parensAreBalanced() {
        int parenDepth = 0;
        for(var c : tokenList) {
            if(c.isOpenBracket()) {
                parenDepth++;
            } else if(c.isCloseBracket()) {
                parenDepth--;
            }

            if(parenDepth < 0) {
                return false;
            }
        }

        return parenDepth == 0;
    }

    public String[] toStringArray() {
        return tokenList.stream().map(Token::toString).toArray(String[]::new);
    }

    public List<Token<?>> toTokenList() {
        return new ArrayList<>(tokenList);
    }

    @Override
    public String toString() {
        return tokenList.stream().map(Token::toString).collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        TokenList that = (TokenList)o;

        return Objects.equals(tokenList, that.tokenList);
    }

    @Override
    public int hashCode() {
        return tokenList != null ? tokenList.hashCode() : 0;
    }

    private static String cleanRawExpression(String rawExpression) {
        String exp = rawExpression;

        // Clear all whitespace
        exp = exp.replaceAll("\\s+", "");

        // Empty string is an invalid expression
        if(exp.length() == 0) {
            throw new InvalidInputException("Error during preprocessing for expression \"" + exp
                    + "\": length of expression was 0.");
        }

        return exp;
    }

    private static List<Token<?>> tokenize(String exp, ParseMode parseMode) {
        if(exp.isEmpty()) {
            return new ArrayList<>();
        }

        Token<?>[] string = new Token[exp.length()];
        Matcher matcher;

        // We must keep track of which characters have already been interpreted, so that characters will not be double
        // interpreted by two or more GraphCharacters
        Set<Integer> expIndicesAlreadyUsed = new HashSet<>();

        matcher = Token.combinedMatcher(exp, parseMode);
        while(matcher.find()) {
            Set<Integer> matchedRange =
                    IntStream.range(matcher.start(), matcher.end())
                            .boxed()
                            .collect(Collectors.toSet());

            // Calculate the intersection between the current matched range and the previous matched ranges
            Set<Integer> rangeIntersection = new HashSet<>(matchedRange);
            rangeIntersection.retainAll(expIndicesAlreadyUsed);

            // If there's no intersection, none of the chars are being doubly interpreted, so we can go ahead and
            // interpret the regex match
            if(rangeIntersection.size() == 0) {
                string[matcher.start()] = Token.newTokenFromString(matcher.group(), parseMode);

                // Add the matched range to the set of previously matched ranges
                expIndicesAlreadyUsed.addAll(matchedRange);
            }

            if(matcher.hitEnd()) {
                break;
            }
        }

        return Arrays.stream(string)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
