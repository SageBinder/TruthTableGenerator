package com.sage.nodes.booleanoperators;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;
import com.sage.nodes.base.Node2;

public class AND extends Node2 {
    public AND(Node parent1, Node parent2) {
        super(parent1, parent2);
    }

    public AND(String tag, Node parent1, Node parent2) {
        super(tag, parent1, parent2);
    }

    @Override
    protected boolean _evaluate(Node parent1, Node parent2, GraphInputs inputs) {
        return parent1.evaluate(inputs) && parent2.evaluate(inputs);
    }
}
