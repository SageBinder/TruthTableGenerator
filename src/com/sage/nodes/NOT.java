package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

public class NOT extends Node {
    public NOT(Node input) {
        super(input);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        if(inputNodes.size() != 1) {
            throw new InvalidInputException();
        }

        return !inputNodes.get(0).evaluate(inputs);
    }
}
