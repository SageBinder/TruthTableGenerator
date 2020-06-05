package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

public class INPUT extends Node {
    public INPUT(String tag) {
        super(tag);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        if(inputs.containsKey(tag)) {
            return inputs.get(tag);
        } else {
            throw new InvalidInputException("Error: input node with the name of \""
                    + tag
                    + "\" could not find a matching input in the inputs map.");
        }
    }
}
