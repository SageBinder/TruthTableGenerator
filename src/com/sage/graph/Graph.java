package com.sage.graph;

import com.sage.graph.expression.ParseMode;
import com.sage.nodes.Node;
import com.sage.nodes.OUTPUT;
import com.sage.nodes.VARIABLE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    public final String rawExp;
    public final String[] variables;

    private final OUTPUT outputNode;

    public Graph(String rawExp, ParseMode parseMode) {
        this.rawExp = rawExp;
        this.outputNode = GraphBuilder.build(rawExp, parseMode);
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
            if(nextNode instanceof VARIABLE var) {
                inputs.add(var.tag);
            } else {
                inputs.addAll(findUniqueVariables(nextNode));
            }
        }

        return inputs;
    }
}
