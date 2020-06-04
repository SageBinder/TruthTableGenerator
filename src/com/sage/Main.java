package com.sage;

import com.sage.graph.Graph;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String expression = "(A & B) | (~C)";
        Graph graph = new Graph(expression);

        System.out.println(expression + "\n");
        System.out.println(generateTruthTable(graph));

//        graph.inputMap.get("A").setValue(true);
//        graph.inputMap.get("B").setValue(true);
//        graph.inputMap.get("C").setValue(false);
//
//        System.out.println(graph.outputNode.evaluate());
    }

    public static String generateTruthTable(Graph graph) {
        String[] variables = graph.inputMap.keySet().toArray(new String[0]);
        double numPermutations = Math.pow(2, variables.length);

        var truthTableString = new StringBuilder(String.join(" | ", List.of(variables)) + " | RESULT\n");

        for(short currPermutation = 0; currPermutation < numPermutations; currPermutation++) {
            for(short varIdx = 0; varIdx < variables.length; varIdx++) {
                graph.inputMap.get(variables[varIdx]).setValue(((1 << varIdx) & currPermutation) > 0);
            }

            boolean result = graph.outputNode.evaluate();

            var currPermutationString = new StringBuilder(Integer.toString(currPermutation, 2));
            currPermutationString
                    .insert(0, "0".repeat(Math.max(0, variables.length - currPermutationString.length())));

            truthTableString
                    .append(String.join(" | ", currPermutationString.reverse().toString().split("")))
                    .append(" | ")
                    .append(result)
                    .append("\n");
        }

        return truthTableString.toString();
    }
}
