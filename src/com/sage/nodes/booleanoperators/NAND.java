package com.sage.nodes.booleanoperators;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;

public class NAND extends AND {
    public NAND(Node parent1, Node parent2) {
        super(parent1, parent2);
    }

    public NAND(String tag, Node parent1, Node parent2) {
        super(tag, parent1, parent2);
    }

    @Override
    protected boolean _evaluate(Node parent1, Node parent2, GraphInputs inputs) {
        return !super._evaluate(parent1, parent2, inputs);
    }
}
