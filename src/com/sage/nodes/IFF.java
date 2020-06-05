package com.sage.nodes;

import com.sage.graph.GraphInputs;

public class IFF extends Node {
    private final Node antecedent;
    private final Node consequent;

    public IFF(Node antecedent, Node consequence) {
        this("", antecedent, consequence);
    }

    public IFF(String tag, Node antecedent, Node consequent) {
        super(tag, antecedent, consequent);
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        return antecedent.evaluate(inputs) == consequent.evaluate(inputs);
    }
}
