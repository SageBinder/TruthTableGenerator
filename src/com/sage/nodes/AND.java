package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class AND extends Node {
    public AND(Node... inputs) {
        super(inputs);
    }

    public AND(String tag, Node... inputNodes) {
        super(tag, inputNodes);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return inputNodes.stream().allMatch(node -> node.evaluate(inputs));
    }
}
