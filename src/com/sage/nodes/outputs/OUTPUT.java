package com.sage.nodes.outputs;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;
import com.sage.nodes.base.Node1;

public class OUTPUT extends Node1 {
    public OUTPUT(Node parent) {
        this("", parent);
    }

    public OUTPUT(String tag, Node parent) {
        super(tag, parent);
    }

    @Override
    public boolean _evaluate(Node parent, GraphInputs inputs) {
        return parent.evaluate(inputs);
    }
}
