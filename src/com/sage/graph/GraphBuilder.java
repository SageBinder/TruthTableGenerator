package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.base.Node;
import com.sage.nodes.outputs.OUTPUT;
import com.sage.token.Token;
import com.sage.token.TokenList;
import com.sage.token.type.base.NodeTokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class GraphBuilder {
    public static OUTPUT build(TokenList tokenList) {
        return new OUTPUT(_build(tokenList));
    }

    private static Node _build(TokenList exp) throws InvalidInputException {
        var expTokenList = exp.toTokenList();
        List<Token<?>> leftTokens = new ArrayList<>();

        for(int i = 0; i < expTokenList.size(); i++) {
            var currToken = expTokenList.get(i);
            var rightTokens = expTokenList.subList(i + 1, expTokenList.size());

            var combinedTokens = new ArrayList<>(leftTokens);
            combinedTokens.add(currToken);
            combinedTokens.addAll(rightTokens);

            int currTokenIdx = leftTokens.size();

            if(currToken.tokenType instanceof NodeTokenType tokenType) {
                var argsInfo = tokenType.getArguments(combinedTokens, currTokenIdx, exp.parseMode);

                int relativeLeftArgIdx = argsInfo.argIndices().relativeLeftIdx();
                int relativeRightArgIdx = argsInfo.argIndices().relativeRightIdx();
                TokenList[] args = argsInfo.args();

                String tag = combinedTokens.subList(currTokenIdx - relativeLeftArgIdx, currTokenIdx + relativeRightArgIdx + 1).stream()
                        .map(Token::toString)
                        .collect(Collectors.joining());
                Node[] parents = List.of(args).stream()
                        .map(GraphBuilder::_build)
                        .toArray(Node[]::new);

                i += relativeRightArgIdx;
                leftTokens = leftTokens.subList(0, leftTokens.size() - relativeLeftArgIdx);

                tokenType.makeNode(tag, currToken.toString(), exp.parseMode, parents);
            }
        }
    }
}
