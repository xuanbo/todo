package com.example.todo.util;

public enum ContentType {

    APPLICATION_JSON("application/json");

    public static final String NAME = "Content-Type";

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}