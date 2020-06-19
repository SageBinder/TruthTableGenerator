package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class EXISTENTIAL_QUANTIFIER extends Node {
    public EXISTENTIAL_QUANTIFIER(Node node) {
        this("", node);
    }

    public EXISTENTIAL_QUANTIFIER(String tag, Node node) {
        super(tag, node);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return false; // TODO
    }
}
