package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

import java.util.Map;

public class VARIABLE extends Node {
    public VARIABLE(String tag) {
        super(tag);
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        var variableMapOptional = inputs.getBooleanVariableMap();

        if(variableMapOptional.isPresent()) {
            Map<String, Boolean> variableMap = variableMapOptional.get();
            if(variableMap.containsKey(tag)) {
                return variableMap.get(tag);
            } else {
                throw new InvalidInputException("Error: VARIABLE node with the name of \""
                        + tag
                        + "\" could not find a matching input in the inputs map.");

            }
        } else {
            throw new InvalidInputException("Error: VARIABLE node with the name of \""
                    + tag
                    + "\" could not find a variable map.");
        }
    }
}
