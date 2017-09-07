package com.example.todo.entity;

import com.example.todo.util.DateFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Todo extends Entity {

    private String title;

    private String content;

    private Boolean completed;

    @JsonFormat(pattern = DateFormatter.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = DateFormatter.TIME_ZONE)
    private Date remindAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(Date remindAt) {
        this.remindAt = remindAt;
    }

}