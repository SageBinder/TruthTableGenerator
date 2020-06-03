package com.sage.nodes;

public class OUTPUT extends Node {
    private final Node inputNode;

    public OUTPUT(Node inputNode) {
        super(inputNode);
        this.inputNode = inputNode;
    }

    @Override
    public boolean evaluate() {
        return inputNode.evaluate();
    }
}
