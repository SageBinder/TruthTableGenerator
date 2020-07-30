package com.sage.graph;

import com.sage.token.parsing.TokenList;
import com.sage.token.parsing.ParseMode;
import com.sage.nodes.outputs.OUTPUT;
import com.sage.nodes.variables.VARIABLE;
import com.sage.nodes.base.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    public final String rawExp;
    public final String[] variables;

    public final TokenList tokenList;

    private final OUTPUT outputNode;

    public Graph(String rawExp, ParseMode parseMode) {
        this.rawExp = rawExp;
        this.tokenList = new TokenList(rawExp, parseMode);
        this.outputNode = GraphBuilder.build(tokenList);
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
        return String.join(delimiter, tokenList.toStringArray());
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
