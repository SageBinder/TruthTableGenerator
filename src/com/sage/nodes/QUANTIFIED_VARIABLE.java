package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;

import java.util.List;

public class QUANTIFIED_VARIABLE extends Node {
    private List<String> inputVariables;
    private String variableName;

    public QUANTIFIED_VARIABLE(String tag, String variableName, List<String> inputVariables) {
        super(tag);
        this.variableName = variableName;
        this.inputVariables = inputVariables;
    }

    @Override
    protected boolean evaluate(GraphInputs inputs) {
        var sentenceMap = inputs.getSentenceMap().orElseThrow(() ->
                new InvalidInputException("Error: QUANTIFIED_VARIABLE node could not find a sentence variable map."));

        if(sentenceMap.containsKey(variableName)) {
            var truthEntries = sentenceMap.get(variableName);
            var boundedVariableMapOptional = inputs.getBoundedVariableMap();
            if(boundedVariableMapOptional.isPresent()) {
                var boundedVariableMap = boundedVariableMapOptional.get();
                var mappedVariables = inputVariables.stream().map(var -> boundedVariableMap.getOrDefault(var, var));
                var thisEntry = new GraphInputs.SentenceMap.TruthEntry(mappedVariables.toArray(String[]::new));

                return truthEntries.contains(thisEntry);
            } else {
                throw new InvalidInputException("Error: QUANTIFIED_VARIABLE node could not find a bounded variable map");
            }
        } else {
            throw new InvalidInputException("Error: QUANTIFIED_VARIABLE node could not find a truth-entry set within the sentence variable map");
        }
    }
}
