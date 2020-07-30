package com.sage.token.type;

import com.sage.nodes.base.Node;
import com.sage.token.ParseMode;
import com.sage.token.type.base.NoArgsNodeTokenType;

import java.util.regex.Pattern;

public enum NodeSupplier implements NoArgsNodeTokenType {
    NODE_SUPPLIER;

    @Override
    public Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents) {
        return null;
    }

    @Override
    public Pattern getPattern(ParseMode parseMode) {
        return null;
    }
}
