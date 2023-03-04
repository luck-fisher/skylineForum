package com.class1.boot.pojo;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Event {
    /**
     * 主题
     */
    private String topic;
    /**
     * 发起事件的用户id
     */
    private Integer userId;

    private Integer entityType;
    private Integer entityId;
    private Integer entityCreateId;
    Map<String,Object> date = new HashMap<>();

    public Event() {
    }

    public Event(String topic, Integer userId, Integer entityType, Integer entityId, Integer entityCreateId, Map<String, Object> date) {
        this.topic = topic;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityCreateId = entityCreateId;
        this.date = date;
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Event setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public Event setEntityType(Integer entityType) {
        this.entityType = entityType;
        return this;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public Event setEntityId(Integer entityId) {
        this.entityId = entityId;
        return this;
    }

    public Integer getEntityCreateId() {
        return entityCreateId;
    }

    public Event setEntityCreateId(Integer entityCreateId) {
        this.entityCreateId = entityCreateId;
        return this;
    }

    public Map<String, Object> getDate() {
        return date;
    }

    public Event setDate(String key,Object value) {
        this.date.put(key,value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityCreateId=" + entityCreateId +
                ", date=" + date +
                '}';
    }
}
