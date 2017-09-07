package com.example.todo.service.impl;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.util.DateFormatter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TodoServiceImpl implements TodoService {

    private static final Logger LOG = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final JDBCClient jdbcClient;

    public TodoServiceImpl(Vertx vertx, JsonObject options) {
        jdbcClient = JDBCClient.createShared(vertx, options);
        initTable().setHandler(ar -> {
            if (ar.succeeded() && ar.result()) {
                LOG.info("init table t_todo success.");
            } else {
                LOG.error("init table t_todo fail.", ar.cause());
            }
        });
    }

    @Override
    public Future<Todo> find(String id) {
        Future<Todo> result = Future.future();
        final String sql = "select * from t_todo where id = ?";
        final JsonArray params = new JsonArray().add(id);
        query(sql, params, result, rs -> {
            List<JsonObject> rows = rs.getRows();
            if (rows.isEmpty()) {
                result.complete(new Todo());
            } else {
                result.complete(rows.get(0).mapTo(Todo.class));
            }
        });
        return result;
    }

    @Override
    public Future<List<Todo>> findAll() {
        Future<List<Todo>> result = Future.future();
        // 查询未完成的代办，按照提醒时间升序排列
        final String sql = "select * from t_todo where completed = 0 order by remindAt asc";
        query(sql, null, result, rs -> {
            List<Todo> todos = rs.getRows().stream()
                    .map(jsonObject -> jsonObject.mapTo(Todo.class))
                    .collect(Collectors.toList());
            result.complete(todos);
        });
        return result;
    }

    @Override
    public Future<List<Todo>> findNeededRemind() {
        Future<List<Todo>> result = Future.future();
        // 查询未完成的代办，按照提醒时间升序排列
        final String sql = "select * from t_todo where completed = 0 and " +
                "remindAt >= (select datetime('now', 'localtime', '+5 minute')) and " +
                "remindAt <= (select datetime('now', 'localtime', '+10 minute')) " +
                "order by remindAt asc";

        query(sql, null, result, rs -> {
            List<Todo> todos = rs.getRows().stream()
                    .map(jsonObject -> jsonObject.mapTo(Todo.class))
                    .collect(Collectors.toList());
            result.complete(todos);
        });
        return result;
    }

    @Override
    public Future<Void> save(Todo todo) {
        final Future<Void> result = Future.future();
        Date current = new Date();
        final String sql = "insert into t_todo(title, content, completed, remindAt, createAt, updateAt) values(?, ?, ?, ?, ?, ?)";
        final JsonArray params = new JsonArray()
                .add(todo.getTitle())
                .add(todo.getContent())
                .add(false)
                .add(DateFormatter.format(todo.getRemindAt()))
                .add(DateFormatter.format(current))
                .add(DateFormatter.format(current));
        update(sql, params, result, ur -> result.complete());
        return result;
    }

    @Override
    public Future<Void> modify(Todo todo) {
        final Future<Void> result = Future.future();
        Date current = new Date();
        final String sql = "update t_todo set title = ?, content = ?, completed = ?, remindAt = ?, updateAt = ? where id = ?";
        final JsonArray params = new JsonArray()
                .add(todo.getTitle())
                .add(todo.getContent())
                .add(todo.getCompleted())
                .add(DateFormatter.format(todo.getRemindAt()))
                .add(DateFormatter.format(current))
                .add(todo.getId());
        update(sql, params, result, ur -> result.complete());
        return result;
    }

    @Override
    public Future<Void> delete(String id) {
        final Future<Void> result = Future.future();
        final String sql = "delete from t_todo where id = ?";
        final JsonArray params = new JsonArray().add(id);
        update(sql, params, result, ur -> result.complete());
        return result;
    }

    /**
     * 初始化表结构
     *
     * @return result future
     */
    private Future<Boolean> initTable() {
        final Future<Boolean> result = Future.future();
        final String sql = "CREATE TABLE IF NOT EXISTS 'main'.'t_todo' (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT DEFAULT NULL," +
                "completed INTEGER DEFAULT 0," +
                "content TEXT DEFAULT NULL," +
                "remindAt DATETIME DEFAULT (datetime('now', 'localtime'))," +
                "createAt DATETIME DEFAULT (datetime('now', 'localtime'))," +
                "updateAt DATETIME DEFAULT (datetime('now', 'localtime'))" +
                ")";
        execute(sql, result, v -> result.complete(true));
        return result;
    }

    /**
     * Executes the given SQL statement
     *
     * @param sql the SQL to execute. For example <code>CREATE TABLE IF EXISTS table ...</code>
     * @param result result future
     * @param handler 处理
     */
    private <T> void execute(String sql, Future<T> result, Handler<Void> handler) {
        jdbcClient.getConnection(connHandle(result, conn -> {
            conn.execute(sql, ar -> {
                if (ar.succeeded()) {
                    handler.handle(ar.result());
                } else {
                    result.fail(ar.cause());
                }
            });
            conn.close();
        }));
    }

    /**
     * query
     *
     * @param sql the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param params these are the parameters to fill the statement.
     * @param result result future
     * @param handler 处理ResultSet
     */
    private <T> void query(String sql, JsonArray params, Future<T> result, Handler<ResultSet> handler) {
        jdbcClient.getConnection(connHandle(result, conn -> {
            conn.queryWithParams(sql, params, ar -> {
                if (ar.succeeded()) {
                    handler.handle(ar.result());
                } else {
                    result.fail(ar.cause());
                }
            });
            conn.close();
        }));
    }

    /**
     * query
     *
     * @param sql the SQL to execute. <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
     * @param params these are the parameters to fill the statement.
     * @param result result future
     * @param handler 处理UpdateResult
     */
    private <T> void update(String sql, JsonArray params, Future<T> result, Handler<UpdateResult> handler) {
        jdbcClient.getConnection(connHandle(result, conn -> {
            conn.updateWithParams(sql, params, ar -> {
                if (ar.succeeded()) {
                    handler.handle(ar.result());
                } else {
                    result.fail(ar.cause());
                }
            });
            conn.close();
        }));
    }

    /**
     * 处理SQLConnection
     *
     * @param future result
     * @param handler 处理SQLConnection
     * @return Handler<AsyncResult<SQLConnection>>
     */
    private <T> Handler<AsyncResult<SQLConnection>> connHandle(Future<T> future, Handler<SQLConnection> handler) {
        return ar -> {
            if (ar.succeeded()) {
                final SQLConnection connection = ar.result();
                handler.handle(connection);
            } else {
                future.fail(ar.cause());
            }
        };
    }

}