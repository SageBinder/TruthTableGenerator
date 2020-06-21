package com.sage;

import com.sage.graph.Graph;
import com.sage.graph.GraphInputs;
import com.sage.graph.expression.ParseMode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String expression = "A|B&&C";
        Graph graph = new Graph(expression, ParseMode.PL);

        System.out.println("Raw expression: " + expression);
        System.out.println("Parsed expression: " + graph.getParsedExpression());
        System.out.println("Parsed expression with spaces: " + graph.getParsedExpression(" ") + "\n");
        System.out.println(generateTruthTable(graph));
    }

    public static String generateTruthTable(Graph graph) {
        String[] variables = graph.variables;
        double numPermutations = variables.length == 0 ? 0 : Math.pow(2, variables.length);

        var truthTableString = new StringBuilder();
        truthTableString
                .append(String.join(" | ", List.of(variables)))
                .append(" | RESULT\n")
                .append(Arrays.stream(variables)
                        .map(var -> "-".repeat(var.length()))
                        .collect(Collectors.joining("-|-")))
                .append("-|-")
                .append("-".repeat("RESULT".length()))
                .append("\n");

        for(short currPermutation = 0; currPermutation < numPermutations; currPermutation++) {
            // Setting up GraphInputs and evaluating graph:
            GraphInputs inputs = new GraphInputs();
            for(short varIdx = 0; varIdx < variables.length; varIdx++) {
                inputs.put(variables[varIdx], ((1 << varIdx) & currPermutation) > 0);
            }

            boolean result = graph.evaluate(inputs);

            // Generating string for this row of the truth table:
            var currPermutationString = new StringBuilder(Integer.toString(currPermutation, 2));
            currPermutationString
                    .insert(0, "0".repeat(Math.max(0, variables.length - currPermutationString.length())));

            var bitsCharList = currPermutationString.reverse().toString().split("");
            for(int i = 0; i < bitsCharList.length; i++) {
                truthTableString
                        .append(bitsCharList[i])
                        .append(" ".repeat(variables[i].length() - 1))
                        .append(" | ");
            }

            truthTableString
                    .append(result)
                    .append("\n");
        }

        return truthTableString.toString();
    }
}
