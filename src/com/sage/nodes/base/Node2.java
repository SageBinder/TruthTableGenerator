package com.sage.nodes.base;

import com.sage.graph.GraphInputs;

public abstract class Node2 extends Node {
    public Node2(Node parent1, Node parent2) {
        super(parent1, parent2);
    }

    public Node2(String tag, Node parent1, Node parent2) {
        super(tag, parent1, parent2);
    }

    @Override
    protected final boolean _evaluate(Node[] parents, GraphInputs inputs) {
        return _evaluate(parents[0], parents[1], inputs);
    }

    protected abstract boolean _evaluate(Node parent1, Node parent2, GraphInputs inputs);
}
