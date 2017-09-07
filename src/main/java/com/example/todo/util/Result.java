package com.example.todo.util;

public class Result<T> {

    private int code;

    private String message;

    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result<String> ok() {
        return ok("ok", "");
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(Code.OK.value(), "ok", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(Code.OK.value(), message, data);
    }

    public static Result<String> fail(String message) {
        return fail(Code.INNER_SERVER_ERROR, message, "");
    }

    public static <T> Result<T> fail(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> fail(Code code, String message, T data) {
        return new Result<>(code.value(), message, data);
    }

    public static Result<String> notFound() {
        return fail(Code.NOT_FOUND,"resource not found", "");
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}