package com.sage.nodes;

import com.sage.graph.GraphInputs;

import java.util.List;

public abstract class Node {
    protected final List<Node> inputNodes;

    public final Node[] getInputNodes() {
        return inputNodes.toArray(new Node[0]);
    }

    protected Node(Node... inputNodes) {
        this.inputNodes = List.of(inputNodes);
    }

    protected abstract boolean evaluate(GraphInputs inputs);
}
