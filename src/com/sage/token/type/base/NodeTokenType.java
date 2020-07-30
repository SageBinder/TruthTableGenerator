package com.sage.token.type.base;

import com.sage.nodes.base.Node;
import com.sage.token.ParseMode;
import com.sage.token.Token;
import com.sage.token.TokenArgs;

import java.util.List;

public interface NodeTokenType extends TokenType {
    Node makeNode(String tag, String tokenString, ParseMode parseMode, Node... parents);

    TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode);
}
