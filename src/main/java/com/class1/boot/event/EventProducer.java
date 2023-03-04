package com.class1.boot.event;

import com.alibaba.fastjson2.JSONObject;
import com.class1.boot.pojo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 点赞、评论、关注事件系统通知的生产者
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     * @param event 当前发生的事件
     */
    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
