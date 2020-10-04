package com.sage.nodes.constants;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node0;

public class TAUTOLOGY extends Node0 {
    public TAUTOLOGY() {
        super();
    }

    public TAUTOLOGY(String tag) {
        super(tag);
    }

    @Override
    protected boolean _evaluate(GraphInputs inputs) {
        return true;
    }
}
