package com.sage.token;

import com.sage.nodes.base.Node;
import com.sage.token.type.base.NodeTokenType;

public class NodeSupplierToken extends Token.NodeToken {
    public NodeSupplierToken(String tokenString, NodeTokenType tokenType, ParseMode parseMode) {
        super(tokenString, tokenType, parseMode);
    }

    public interface Supplier {
        <T extends Node> T get();
    }
}
