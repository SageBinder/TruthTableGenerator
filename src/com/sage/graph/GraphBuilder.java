package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.nodes.INPUT;
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
                exp.throwError("No operator was found, but the number of variables was > 1 (should be == 1");
            } else if(exp.numVariables() < 1) {
                exp.throwError("No operator was found, but the number of variables was < 1 (should be == 1)");
            }

            GraphCharacter soleVariable = exp.findFirstVariable()
                    .orElseThrow(() -> exp.generateError("No operator was found, but no variable was found either"));
            return new INPUT(soleVariable.toString());
        }

        Operator topLevelOperator = Operator.fromChar(exp.charAt(topOperatorIdx));
        return topLevelOperator.newNode(
                exp.toString(),
                topLevelOperator.requiresLeftArg ? _build(exp.getLeftOperand(topOperatorIdx)) : null,
                topLevelOperator.requiresRightArg ? _build(exp.getRightOperand(topOperatorIdx)) : null);
    }
}
