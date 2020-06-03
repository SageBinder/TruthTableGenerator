package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;

public class NOT extends Node {
    public NOT(Node input) {
        super(input);
    }

    @Override
    protected boolean evaluate() {
        if(inputNodes.size() != 1) {
            throw new InvalidInputException();
        }

        return !inputNodes.get(0).evaluate();
    }
}
