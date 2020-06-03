package com.sage.nodes;

public class AND extends Node {
    public AND(Node... inputs) {
        super(inputs);
    }

    @Override
    protected boolean evaluate() {
        return inputNodes.stream().allMatch(Node::evaluate);
    }
}
