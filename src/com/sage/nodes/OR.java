package com.sage.nodes;

public class OR extends Node {
    public OR(Node... inputs) {
        super(inputs);
    }

    @Override
    protected boolean evaluate() {
        return inputNodes.stream().anyMatch(Node::evaluate);
    }
}
