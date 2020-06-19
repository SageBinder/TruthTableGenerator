package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class UNIVERSAL_QUANTIFIER extends Node {
    public UNIVERSAL_QUANTIFIER(Node node) {
        this("", node);
    }

    public UNIVERSAL_QUANTIFIER(String tag, Node node) {
        super(tag, node);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return false; // TODO
    }
}
