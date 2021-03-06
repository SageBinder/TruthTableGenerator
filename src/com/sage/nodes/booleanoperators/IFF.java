package com.sage.nodes.booleanoperators;

import com.sage.graph.GraphInputs;
import com.sage.nodes.base.Node;
import com.sage.nodes.base.Node2;

public class IFF extends Node2 {
    public IFF(Node antecedent, Node consequence) {
        this("", antecedent, consequence);
    }

    public IFF(String tag, Node antecedent, Node consequent) {
        super(tag, antecedent, consequent);
    }

    @Override
    protected boolean _evaluate(Node antecedent, Node consequent, GraphInputs inputs) {
        return antecedent.evaluate(inputs) == consequent.evaluate(inputs);
    }
}
