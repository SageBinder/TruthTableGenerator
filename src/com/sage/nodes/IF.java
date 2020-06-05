package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class IF extends Node {
    private final Node antecedent;
    private final Node consequent;

    public IF(Node antecedent, Node consequence) {
        this("", antecedent, consequence);
    }

    public IF(String tag, Node antecedent, Node consequent) {
        super(tag, antecedent, consequent);
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return !antecedent.evaluate(inputs) || consequent.evaluate(inputs);
    }
}
