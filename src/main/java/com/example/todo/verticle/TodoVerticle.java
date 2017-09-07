package com.example.todo.verticle;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.service.impl.TodoServiceImpl;
import com.example.todo.util.Accept;
import com.example.todo.util.Validator;
import com.example.todo.util.Response;
import com.example.todo.util.Result;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class TodoVerticle extends RestVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TodoVerticle.class);
    private static final String REMIND_EVENT = "todo.remind";

    private TodoService todoService;

    public TodoVerticle() {
        super(8080);
    }

    @Override
    protected void initData() {
        JsonObject options = new JsonObject()
                .put("url", "jdbc:sqlite:todo.db")
                .put("driver_class", "org.sqlite.JDBC");
        todoService = new TodoServiceImpl(vertx, options);

        todoRemind();
    }

    @Override
    protected void registerRoutes(Router router) {
        Accept.json(router.get("/todo/:id")).handler(this::find);
        Accept.json(router.get("/todos")).handler(this::findAll);
        Accept.json(router.post("/todo")).handler(this::save);
        Accept.json(router.put("/todo")).handler(this::modify);
        Accept.json(router.delete("/todo/:id")).handler(this::delete);
    }

    private void find(RoutingContext rc) {
        String id = rc.request().getParam("id");

        Validator.notEmpty(id, "id required.");

        todoService.find(id).setHandler(ar -> {
            if (ar.succeeded()) {
                Response.sendJson(rc, Result.ok(ar.result()));
            } else {
                rc.fail(ar.cause());
            }
        });
    }

    private void findAll(RoutingContext rc) {
        todoService.findAll().setHandler(ar -> {
            if (ar.succeeded()) {
                Response.sendJson(rc, Result.ok(ar.result()));
            } else {
                rc.fail(ar.cause());
            }
        });
    }

    private void save(RoutingContext rc) {
        Todo todo = rc.getBodyAsJson().mapTo(Todo.class);

        Validator.notEmpty(todo.getTitle(), "title required.");
        Validator.notEmpty(todo.getContent(), "content required.");
        Validator.notNull(todo.getRemindAt(), "remindAt required.");

        todoService.save(todo).setHandler(ar -> {
            if (ar.succeeded()) {
                Response.sendJson(rc, Result.ok());
            } else {
                rc.fail(ar.cause());
            }
        });
    }

    private void modify(RoutingContext rc) {
        Todo todo = rc.getBodyAsJson().mapTo(Todo.class);

        Validator.notEmpty(todo.getTitle(), "title required.");
        Validator.notEmpty(todo.getContent(), "content required.");
        Validator.notNull(todo.getRemindAt(), "remindAt required.");
        Validator.notNull(todo.getCompleted(), "completed required.");

        todoService.modify(todo).setHandler(ar -> {
            if (ar.succeeded()) {
                Response.sendJson(rc, Result.ok());
            } else {
                rc.fail(ar.cause());
            }
        });
    }

    private void delete(RoutingContext rc) {
        String id = rc.request().getParam("id");

        Validator.notEmpty(id, "id required.");

        todoService.delete(id).setHandler(ar -> {
            if (ar.succeeded()) {
                Response.sendJson(rc, Result.ok());
            } else {
                rc.fail(ar.cause());
            }
        });
    }

    private void todoRemind() {
        long delay = 5 * 60 * 1000L;
        vertx.setPeriodic(delay, timerId ->
            todoService.findNeededRemind().setHandler(ar -> {
               if (ar.succeeded()) {
                   if (!ar.result().isEmpty()) {
                       vertx.eventBus().send(REMIND_EVENT, Json.encode(ar.result()));
                       LOG.info("remind start.");
                   } else {
                       LOG.info("nothing remind.");
                   }
               } else {
                   LOG.warn("remind exception.", ar.cause());
               }
            })
        );

        vertx.eventBus().<String>consumer(REMIND_EVENT, message -> {
            JsonArray jsonArray = new JsonArray(message.body());
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                Todo todo = jsonArray.getJsonObject(i).mapTo(Todo.class);
                LOG.info(todo);
            }
            LOG.info("remind end.");
        });
    }

}