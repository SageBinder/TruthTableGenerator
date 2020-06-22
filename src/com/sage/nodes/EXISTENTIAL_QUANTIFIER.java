package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

public class EXISTENTIAL_QUANTIFIER extends Node {
    private final Node node;
    private final String bindingVariable;

    public EXISTENTIAL_QUANTIFIER(String tag, String bindingVariable, Node node) {
        super(tag, node);
        this.node = node;
        this.bindingVariable = bindingVariable;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        var UD = inputs.getUD().orElseThrow(() ->
                new InvalidInputException("Error: EXISTENTIAL_QUANTIFIER node could not find any universe of discourse"));

        GraphInputs.BoundedVariableMap currentMap = inputs.getBoundedVariableMap().orElse(new GraphInputs.BoundedVariableMap());

        return UD.stream().anyMatch(var -> {
            GraphInputs.BoundedVariableMap newMap = new GraphInputs.BoundedVariableMap(currentMap);
            newMap.put(bindingVariable, var);
            inputs.setBoundedVariableMap(newMap);

            return node.evaluate(inputs);
        });
    }
}
