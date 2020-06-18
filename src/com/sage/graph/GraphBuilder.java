package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.expression.GraphString;
import com.sage.graph.expression.Operator;
import com.sage.graph.expression.Variable;
import com.sage.nodes.Node;
import com.sage.nodes.OUTPUT;

class GraphBuilder {
    public static OUTPUT build(String rawExpression) {
        return new OUTPUT(rawExpression, _build(new GraphString(rawExpression)));
    }

    private static Node _build(GraphString exp) throws InvalidInputException {
        int topOperatorIdx = exp.indexOfTopLevelOperator();
        if(topOperatorIdx == -1) {
            if(exp.numVariables() > 1) {
                exp.throwError("No operator was found, but the number of variables was > 1 (should be == 1)");
            } else if(exp.numVariables() < 1) {
                exp.throwError("No operator was found, but the number of variables was < 1 (should be == 1)");
            }

            Variable soleVariable = exp.findFirstVariable()
                    .orElseThrow(() -> exp.generateError("No operator was found, but no variable was found either"));
            return soleVariable.makeNode(soleVariable.toString(), null, null);
        }

        if(exp.charAt(topOperatorIdx) instanceof Operator opChar) {
            return opChar.makeNode(
                    exp.toString(),
                    opChar.requiresLeftArg() ? _build(exp.getLeftOperand(topOperatorIdx)) : null,
                    opChar.requiresRightArg() ? _build(exp.getRightOperand(topOperatorIdx)) : null);

        } else {
            throw exp.generateError("In _build(GraphString exp), topOperatorIdx was >=0, " +
                    "however the char at topOperatorIdx was not an operator. topOperatorIdx = " +
                    topOperatorIdx +
                    ", exp.charAt(topOperatorIdx) = " +
                    exp.charAt(topOperatorIdx));
        }
    }
}
