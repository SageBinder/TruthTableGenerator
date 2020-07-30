package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.base.Node;
import com.sage.nodes.outputs.OUTPUT;
import com.sage.token.tokens.NodeSupplierToken;
import com.sage.token.tokens.NodeToken;
import com.sage.token.tokens.Token;
import com.sage.token.parsing.TokenList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class GraphBuilder {
    public static OUTPUT build(TokenList tokenList) {
        return new OUTPUT(_build(tokenList));
    }

    // It's almost 3 in the morning and I'm too lazy to comment this. I REALLY should do it eventually, but to be honest,
    // I probably won't. I apologize to my future self.
    // (Also I don't even know if it's fucking working; I just wanna sleep)
    private static Node _build(TokenList exp) throws InvalidInputException {
        var expTokenList = exp.toTokenList();
        List<NodeSupplierToken> leftTokens = new ArrayList<>();

        for(int i = 0; i < expTokenList.size(); i++) {
            var currToken = expTokenList.get(i);
            var rightTokens = expTokenList.subList(i + 1, expTokenList.size());

            var combinedTokens = new ArrayList<Token<?>>(leftTokens);
            combinedTokens.add(currToken);
            combinedTokens.addAll(rightTokens);

            int currTokenIdx = leftTokens.size();

            if(currToken instanceof NodeToken nodeToken) {
                var argsInfo = nodeToken.tokenType.getArguments(combinedTokens, currTokenIdx, exp.parseMode);

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

                leftTokens.add(new NodeSupplierToken(nodeToken.makeNode(tag, parents), exp.parseMode));
            }
        }

        if(leftTokens.size() != 1) {
            throw new InvalidInputException("For expression \"" + exp + "\", leftTokens.size() = " + leftTokens.size() + " (should be 1)");
        }

        return leftTokens.get(0).makeNode();
    }
}
