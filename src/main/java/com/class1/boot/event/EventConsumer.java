package com.class1.boot.event;

import com.alibaba.fastjson2.JSONObject;
import com.class1.boot.pojo.Event;
import com.class1.boot.pojo.Message;
import com.class1.boot.service.MessageService;
import com.class1.boot.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费事件，产生系统通知
 */
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    /**
     * 处理生产者产生的事件
     * @param record 生产者发送的消息
     */
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handleEvent(ConsumerRecord record) {
        //收到的通知为空，写入日志，返回
        if (record == null||record.value() == null) {
            logger.error("发送消息为空！");
            return;
        }
        //转换后的事件为空，说明格式错误，写入日志，返回
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            logger.error("消息格式错误！");
            return;
        }
        //发送系统通知
        Message message = new Message();
        message.setStatus(1);
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityCreateId());
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());
        //其他信息统一存入map
        Map<String,Object> content = new HashMap<>(16);
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        //如果事件的dateMap中存在其他值，也存入map中
        if (!event.getDate().isEmpty()){
            content.putAll(event.getDate());
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
