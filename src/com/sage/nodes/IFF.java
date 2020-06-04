package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class IFF extends Node {
    private final Node antecedent;
    private final Node consequent;

    public IFF(Node antecedent, Node consequence) {
        super(antecedent, consequence);
        this.antecedent = antecedent;
        this.consequent = consequence;

    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return antecedent.evaluate(inputs) == consequent.evaluate(inputs);
    }
}
