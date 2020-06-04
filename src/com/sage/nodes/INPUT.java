package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

public class INPUT extends Node {
    public final String name;

    public INPUT(String name) {
        this.name = name;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        if(inputs.containsKey(name)) {
            return inputs.get(name);
        } else {
            throw new InvalidInputException("Error: input node with the name of \""
                    + name
                    + "\" could not find a matching input in the inputs map.");
        }
    }
}
