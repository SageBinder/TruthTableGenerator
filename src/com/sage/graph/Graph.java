package com.sage.graph;

import com.sage.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    public final String rawExp;
    public final OUTPUT outputNode;

    public final Map<String, INPUT> inputMap;

    public Graph(String rawExp) {
        this.rawExp = rawExp;
        this.outputNode = GraphBuilder.build(rawExp);
        this.inputMap = generateInputNodes(outputNode);
    }

    private static Map<String, INPUT> generateInputNodes(Node outputNode) {
        Map<String, INPUT> inputs = new HashMap<>();
        var inputPipes = getInputPipesRecursively(outputNode);

        for(var inputPipe : inputPipes) {
            if(inputs.containsKey(inputPipe.name)) {
                inputPipe.setInput(inputs.get(inputPipe.name));
            } else {
                var newInput = new INPUT();
                inputs.put(inputPipe.name, newInput);
                inputPipe.setInput(newInput);
            }
        }

        return inputs;
    }

    private static List<NAMED_INPUT_PIPE> getInputPipesRecursively(Node outputNode) {
        List<NAMED_INPUT_PIPE> inputPipes = new ArrayList<>();

        for(Node node : outputNode.getInputNodes()) {
            if(node instanceof NAMED_INPUT_PIPE inputPipe) {
                inputPipes.add(inputPipe);
            } else {
                inputPipes.addAll(getInputPipesRecursively(node));
            }
        }

        return inputPipes;
    }
}
