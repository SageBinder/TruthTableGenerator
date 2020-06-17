package com.sage.graph;

import com.sage.nodes.*;

import java.util.*;

public class Graph {
    public final String rawExp;
    public final String[] variables;

    private final OUTPUT outputNode;

    public Graph(String rawExp) {
        this.rawExp = rawExp;
        this.outputNode = GraphBuilder.build(rawExp);
        this.variables = findUniqueVariables(outputNode).toArray(new String[0]);
        Arrays.sort(variables);
    }

    public boolean evaluate(GraphInputs inputs) {
        return outputNode.evaluate(inputs);
    }

    public String getParsedExpression() {
        return outputNode.getInputNodes()[0].tag;
    }

    public OUTPUT getOutputNode() {
        return outputNode;
    }

    private static Set<String> findUniqueVariables(Node currNode) {
        Set<String> inputs = new HashSet<>();

        for(Node nextNode : currNode.getInputNodes()) {
            if(nextNode instanceof INPUT input) {
                inputs.add(input.tag);
            } else {
                inputs.addAll(findUniqueVariables(nextNode));
            }
        }

        return inputs;
    }
}
