package com.sage.graph.expression;

// Represents a graph character which can be made into a node.
// For example, "|" can be made into a node because it's an operator (OR). A parenthesis, however, could not be made
// into a node.

import com.sage.nodes.Node;

abstract class NodeGraphCharacter extends GraphCharacter {
    public NodeGraphCharacter(String character) {
        super(character);
    }

    public NodeGraphCharacter(GraphCharacter other) {
        super(other);
    }

    public final Node makeNode(Node left, Node right) {
        return makeNode("", left, right);
    }

    public abstract Node makeNode(String tag, Node left, Node right);

    public abstract boolean requiresLeftArg();

    public abstract boolean requiresRightArg();
}
