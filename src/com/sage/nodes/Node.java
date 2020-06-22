package com.sage.nodes;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.GraphInputs;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public abstract class Node {
    public final String tag;
    protected final List<Node> inputNodes;

    protected Node(Node... inputNodes) {
        this("", inputNodes);
    }

    protected Node(String tag, Node... inputNodes) {
        if(ArrayUtils.contains(inputNodes, null)) {
            throw new InvalidInputException("Error: Node constructor received a null node as input");
        }

        this.inputNodes = List.of(inputNodes);
        this.tag = tag;
    }

    public final Node[] getInputNodes() {
        return inputNodes.toArray(new Node[0]);
    }

    protected abstract boolean evaluate(GraphInputs inputs);

    @Override
    public String toString() {
        return tag;
    }
}
