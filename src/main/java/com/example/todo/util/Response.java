package com.example.todo.util;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class Response {

    public static <T> void sendJson(RoutingContext rc, T data) {
        rc.response().putHeader(ContentType.NAME, ContentType.APPLICATION_JSON.value()).end(Json.encode(data));
    }

}