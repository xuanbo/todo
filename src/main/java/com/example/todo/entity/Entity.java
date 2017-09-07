package com.example.todo.entity;

import com.example.todo.util.DateFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public abstract class Entity implements Serializable {

    private String id;

    @JsonFormat(pattern = DateFormatter.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = DateFormatter.TIME_ZONE)
    private Date createAt;

    @JsonFormat(pattern = DateFormatter.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = DateFormatter.TIME_ZONE)
    private Date updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}