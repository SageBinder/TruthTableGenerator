package com.sage.graph;

import com.sage.exceptions.InvalidInputException;
import com.sage.graph.expression.GraphString;
import com.sage.nodes.OUTPUT;
import com.sage.nodes.base.Node;

class GraphBuilder {
    public static OUTPUT build(GraphString graphString) {
        return new OUTPUT(_build(graphString));
    }

    private static Node _build(GraphString exp) throws InvalidInputException {
        int topOperatorIdx = exp.indexOfTopLevelOperator();
        if(topOperatorIdx == -1) {
            exp.throwError("No operator found!");
        }

        var opChar = exp.charAt(topOperatorIdx);
        if(opChar.isOperator()) {
            return opChar.makeNode(
                    exp.toString(),
                    opChar.requiresLeftArg() ? _build(exp.getLeftOperand(topOperatorIdx)) : null,
                    opChar.requiresRightArg() ? _build(exp.getRightOperand(topOperatorIdx)) : null,
                    exp.parseMode);

        } else {
            throw exp.generateError("In _build(GraphString exp), topOperatorIdx was >=0, " +
                    "however the char at topOperatorIdx was not an operator. topOperatorIdx = " +
                    topOperatorIdx +
                    ", exp.charAt(topOperatorIdx) = " +
                    opChar);
        }
    }
}
