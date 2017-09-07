package com.example.todo.util;

public enum Code {
    OK(200),
    PARAMETER_NOT_CORRECT(400),
    NOT_FOUND(404),
    INNER_SERVER_ERROR(500)
    ;

    private int value;

    Code(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}