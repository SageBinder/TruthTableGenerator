package com.sage.nodes;

public class NAMED_INPUT_PIPE extends Node {
    public final String name;
    private INPUT input;

    public NAMED_INPUT_PIPE(String name) {
        this.name = name;
    }

    public void setInput(INPUT input) {
        this.input = input;
    }

    public INPUT getInput() {
        return input;
    }

    @Override
    protected boolean evaluate() {
        return input.getValue();
    }
}
