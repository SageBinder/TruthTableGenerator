package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class IF extends Node {
    private final Node antecedent;
    private final Node consequent;

    public IF(Node antecedent, Node consequence) {
        super(antecedent, consequence);
        this.antecedent = antecedent;
        this.consequent = consequence;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return !antecedent.evaluate(inputs) || consequent.evaluate(inputs);
    }
}
