package com.example.todo.verticle;

import com.example.todo.exception.ValidationException;
import com.example.todo.util.Code;
import com.example.todo.util.Response;
import com.example.todo.util.Result;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.DecodeException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class RestVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(RestVerticle.class);

    private final int port;

    public RestVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start() throws Exception {
        initData();

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        registerRoutes(router);

        router.route().handler(this::notFound);
        router.route().failureHandler(this::failureHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        LOG.info("start server on " + port);
    }

    protected abstract void initData();
    protected abstract void registerRoutes(Router router);

    private void notFound(RoutingContext rc) {
        Response.sendJson(rc, Result.notFound());
    }

    private void failureHandler(RoutingContext rc) {
        int code = rc.statusCode();
        Throwable failure = rc.failure();
        if (failure instanceof DecodeException) {
            LOG.warn("failureHandler catch DecodeException.");
            Response.sendJson(rc, Result.fail(Code.PARAMETER_NOT_CORRECT, "parameter is incorrect", ""));
        } else if (failure instanceof ValidationException) {
            LOG.warn("failureHandler catch ValidationException.");
            Response.sendJson(rc, Result.fail(Code.PARAMETER_NOT_CORRECT, failure.getMessage(), ""));
        } else {
            LOG.warn("failureHandler catch.", failure);
            Response.sendJson(rc, Result.fail(code, failure.getMessage(), ""));
        }
    }
}