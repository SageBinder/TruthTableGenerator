package com.sage;

import com.sage.graph.Graph;
import com.sage.graph.GraphInputs;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String expression = "A > B";
        Graph graph = new Graph(expression);

        System.out.println(expression + "\n");
        System.out.println(generateTruthTable(graph));
    }

    public static String generateTruthTable(Graph graph) {
        String[] variables = graph.variables;
        double numPermutations = Math.pow(2, variables.length);

        var truthTableString = new StringBuilder(String.join(" | ", List.of(variables)) + " | RESULT\n");

        for(short currPermutation = 0; currPermutation < numPermutations; currPermutation++) {
            GraphInputs inputs = new GraphInputs();
            for(short varIdx = 0; varIdx < variables.length; varIdx++) {
                inputs.put(variables[varIdx], ((1 << varIdx) & currPermutation) > 0);
            }

            boolean result = graph.evaluate(inputs);

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
