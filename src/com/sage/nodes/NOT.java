package com.sage.nodes;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;
import com.sage.nodes.base.Node1;

public class NOT extends Node1 {
    public NOT(Node input) {
        super(input);
    }

    public NOT(String tag, Node input) {
        super(tag, input);
    }

    @Override
    protected boolean _evaluate(Node parent, GraphInputs inputs) {
        return !parent.evaluate(inputs);
    }
}
