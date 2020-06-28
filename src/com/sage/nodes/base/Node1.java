package com.sage.nodes.base;

import com.sage.graph.GraphInputs;

public abstract class Node1 extends Node {
    public Node1(Node parent) {
        super(parent);
    }

    public Node1(String tag, Node parent) {
        super(tag, parent);
    }

    @Override
    final boolean _evaluate(Node[] parents, GraphInputs inputs) {
        return _evaluate(parents[0], inputs);
    }

    protected abstract boolean _evaluate(Node parent, GraphInputs inputs);
}
