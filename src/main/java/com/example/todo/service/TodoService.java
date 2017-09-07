package com.example.todo.service;

import com.example.todo.entity.Todo;
import io.vertx.core.Future;

import java.util.List;

public interface TodoService {

    Future<Todo> find(String id);

    Future<List<Todo>> findAll();

    Future<List<Todo>> findNeededRemind();

    Future<Void> save(Todo todo);

    Future<Void> modify(Todo todo);

    Future<Void> delete(String id);

}