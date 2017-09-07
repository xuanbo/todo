package com.example.todo.util;

import io.vertx.ext.web.Route;

public class Accept {

    public static Route json(Route route) {
        return route.consumes(ContentType.APPLICATION_JSON.value()).produces(ContentType.APPLICATION_JSON.value());
    }

    public static void main(String[] args) {
        System.out.println(ContentType.APPLICATION_JSON.value());
        System.out.println(ContentType.APPLICATION_JSON.name());
    }
}