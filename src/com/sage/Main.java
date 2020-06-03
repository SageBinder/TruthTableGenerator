package com.sage;

import com.sage.graph.Graph;

public class Main {

    public static void main(String[] args) {
        String expression = "A & B | ~C";
        Graph graph = new Graph(expression);

        graph.inputMap.get("A").setValue(false);
        graph.inputMap.get("B").setValue(false);
        graph.inputMap.get("C").setValue(true);

        System.out.println(graph.outputNode.evaluate());
    }
}
