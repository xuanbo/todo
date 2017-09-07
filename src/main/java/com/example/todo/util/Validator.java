package com.example.todo.util;

import com.example.todo.exception.ValidationException;

import java.util.Collection;

public class Validator {

    public static <T> void notNull(T expect) {
        notNull(expect, "argument required.");
    }

    public static <T> void notNull(T expect, String message) {
        if (null == expect)
            throw new ValidationException(message);
    }

    public static void notEmpty(String expect) {
        notEmpty(expect, "parameter cannot be empty.");
    }

    public static void notEmpty(String expect, String message) {
        notNull(expect);
        if (expect.isEmpty())
            throw new ValidationException(message);
    }

    public static <T> void notEmpty(Collection<T> expect) {
        notEmpty(expect, "parameter cannot be empty.");
    }

    public static <T> void notEmpty(Collection<T> expect, String message) {
        notNull(expect);
        if (expect.isEmpty())
            throw new ValidationException(message);
    }

    public static void positiveNumber(String expect) {
        positiveNumber(expect, "require positive number.");
    }

    public static void positiveNumber(String expect, String message) {
        regex(expect, "^([1-9][0-9]*)$", message);
    }

    public static void regex(String expect, String regex) {
        regex(expect, regex, "not match regex.");
    }

    public static void regex(String expect, String regex, String message) {
        notEmpty(expect, message);
        if (!expect.matches(regex)) {
            throw new ValidationException(message);
        }
    }
}