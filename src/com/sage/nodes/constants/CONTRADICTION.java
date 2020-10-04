package com.sage.nodes.constants;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node0;

public class CONTRADICTION extends Node0 {
    public CONTRADICTION() {
        super();
    }

    public CONTRADICTION(String tag) {
        super(tag);
    }

    @Override
    protected boolean _evaluate(GraphInputs inputs) {
        return false;
    }
}
