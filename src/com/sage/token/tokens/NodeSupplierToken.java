package com.sage.token.tokens;

import com.sage.nodes.base.Node;
import com.sage.token.parsing.ParseMode;
import com.sage.token.parsing.TokenArgIndices;
import com.sage.token.parsing.TokenArgs;
import com.sage.token.parsing.TokenList;
import com.sage.token.type.base.NodeTokenType;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.regex.Pattern;

// This class exists specifically to aid in graph creation. Its sole purpose is to override the makeNode() function and
// return the node that it received in its constructor. This is to help collapse multiple tokens into a single token,
// which is necessary for nesting purposes.

// Jesus fuck it's so hacky, I wanna die.
public class NodeSupplierToken extends NodeToken {
    private final Node node;

    public NodeSupplierToken(Node node, ParseMode parseMode) {
        super(node.tag, new NodeTokenType() {
            @Override
            public Node makeNode(String tag, String tokenString1, ParseMode parseMode1, Node... parents) {
                return node;
            }

            @Override
            public TokenArgs getArguments(List<Token<?>> tokenList, int idx, ParseMode parseMode1) {
                return new TokenArgs(new TokenArgIndices(0, 0), new TokenList[0]);
            }

            @Override
            public Pattern getPattern(ParseMode parseMode1) {
                throw new NotImplementedException();
            }
        }, parseMode);
        this.node = node;
    }

    public Node makeNode() {
        return makeNode(null, (Node)null);
    }

    @Override
    public Node makeNode(String tag, Node... parents) {
        return node;
    }
}
