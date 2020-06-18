package com.sage.nodes;

import com.sage.graph.GraphInputs;

import java.util.List;

public abstract class Node {
    public final String tag;
    protected final List<Node> inputNodes;

    protected Node(Node... inputNodes) {
        this("", inputNodes);
    }

    protected Node(String tag, Node... inputNodes) {
        this.inputNodes = List.of(inputNodes);
        this.tag = tag;
    }

    public final Node[] getInputNodes() {
        return inputNodes.toArray(new Node[0]);
    }

    protected abstract boolean evaluate(GraphInputs inputs);

    public interface NodeConstructor<T extends Node> {
        default T newNode(Node... inputs) {
            return newNode("", inputs);
        }

        T newNode(String tag, Node... inputs);
    }
}
