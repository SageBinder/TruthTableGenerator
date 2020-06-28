package com.sage.nodes.base;

import com.sage.graph.GraphInputs;

import java.util.Objects;

public abstract class Node {
    public final String tag;
    private final Node[] parents;

    public Node(Node... parents) {
        this("", parents);
    }

    public Node(String tag, Node... parents) {
        for(var obj : parents) {
            Objects.requireNonNull(obj);
        }

        this.tag = tag;
        this.parents = parents;
    }

    public final Node[] getParents() {
        return parents.clone();
    }

    public final boolean evaluate(GraphInputs inputs) {
        return _evaluate(parents, inputs);
    }

    abstract boolean _evaluate(Node[] parents, GraphInputs inputs);

    @Override
    public String toString() {
        return tag;
    }
}
