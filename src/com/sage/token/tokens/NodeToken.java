package com.sage.token.tokens;

import com.sage.nodes.base.Node;
import com.sage.token.parsing.ParseMode;
import com.sage.token.type.base.NodeTokenType;

public non-sealed class NodeToken extends Token<NodeTokenType> {
    protected NodeToken(String tokenString, NodeTokenType tokenType, ParseMode parseMode) {
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
