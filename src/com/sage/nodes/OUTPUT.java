package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class OUTPUT extends Node {
    private final Node inputNode;

    public OUTPUT(Node inputNode) {
        super(inputNode);
        this.inputNode = inputNode;
    }

    @Override
    public boolean evaluate(GraphInputs inputs) {
        return inputNode.evaluate(inputs);
    }
}
