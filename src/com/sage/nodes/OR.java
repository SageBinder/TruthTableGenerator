package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class OR extends Node {
    public OR(Node... inputs) {
        super(inputs);
    }

    public OR(String tag, Node... inputNodes) {
        super(tag, inputNodes);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return inputNodes.stream().anyMatch(node -> node.evaluate(inputs));
    }
}
