package com.sage.nodes;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;
import com.sage.nodes.base.Node2;

public class OR extends Node2 {
    public OR(Node parentNode1, Node parentNode2) {
        super(parentNode1, parentNode2);
    }

    public OR(String tag, Node parentNode1, Node parentNode2) {
        super(tag, parentNode1, parentNode2);
    }

    @Override
    protected boolean _evaluate(Node parent1, Node parent2, GraphInputs inputs) {
        return parent1.evaluate(inputs) | parent2.evaluate(inputs);
    }
}
