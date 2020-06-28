package com.sage.graph;

import com.sage.graph.expression.GraphString;
import com.sage.graph.expression.ParseMode;
import com.sage.nodes.OUTPUT;
import com.sage.nodes.VARIABLE;
import com.sage.nodes.base.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    public final String rawExp;
    public final String[] variables;

    public final GraphString graphString;

    private final OUTPUT outputNode;

    public Graph(String rawExp, ParseMode parseMode) {
        this.rawExp = rawExp;
        this.graphString = new GraphString(rawExp, parseMode);
        this.outputNode = GraphBuilder.build(graphString);
        this.variables = findUniqueVariables(outputNode).toArray(new String[0]);
        Arrays.sort(variables);
    }

    public boolean evaluate(GraphInputs inputs) {
        return outputNode.evaluate(inputs);
    }

    public String getParsedExpression() {
        return getParsedExpression("");
    }

    public String getParsedExpression(String delimiter) {
        return String.join(delimiter, graphString.toStringArray());
    }

    public OUTPUT getOutputNode() {
        return outputNode;
    }

    private static Set<String> findUniqueVariables(Node currNode) {
        Set<String> inputs = new HashSet<>();

        for(Node nextNode : currNode.getParents()) {
            if(nextNode instanceof VARIABLE var) {
                inputs.add(var.tag);
            } else {
                inputs.addAll(findUniqueVariables(nextNode));
            }
        }

        return inputs;
    }
}
