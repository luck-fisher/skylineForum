package com.class1.boot.service.impl;

import com.class1.boot.dao.mappers.MessageMapper;
import com.class1.boot.pojo.Message;
import com.class1.boot.service.MessageService;
import com.class1.boot.util.SensitiveFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filterSensitiveKeyword(message.getContent()));
        return messageMapper.addMessage(message);
    }

    @Override
    public int updateLetterStatus(List<Integer> ids, String conversationId, Integer status) {
        return messageMapper.updateStatus(ids, conversationId, status);
    }

    @Override
    public List<Integer> getLetterIds(List<Message> letterList,Integer userId) {
        if(letterList==null){
            return null;
        }
        List<Integer> ids = new ArrayList<>();
        for (Message message : letterList) {
            if (Objects.equals(userId, message.getToId()) && message.getStatus()==1){
                ids.add(message.getId());
            }
        }
        return ids;
    }

    @Override
    public PageInfo<Map<String, Object>> getAllMessage(Integer userId,Integer pageNum) {

        PageHelper.startPage(pageNum,10);
        List<Map<String, Object>> messageList = messageMapper.getMessageList(userId);

        return new PageInfo<>(messageList, 5);
    }

    @Override
    public int getMessageRows(Integer userId) {
        return messageMapper.getMessageRows(userId);
    }

    @Override
    public List<Message> getAllLetter(String conversationId) {
        return messageMapper.getAllLetter(conversationId);
    }

    @Override
    public int getLetterRows(String conversationId) {
        return messageMapper.getLetterRows(conversationId);
    }

    /**
     * 得到某个主题系统通知数量
     *
     * @param topic  主题
     * @param userId 当前用户的id
     * @return 某个主题系统通知数量
     */
    @Override
    public int getNoticeMessageCount(String topic, Integer userId) {
        return messageMapper.getAllNoticeMessageCount(topic,userId);
    }

    /**
     * 得到未读系统通知的数量
     *
     * @param topic  主题，未传入则为全部
     * @param userId 当前用户id
     * @return 未读系统通知的数量
     */
    @Override
    public int getNewNoticeMessageCount(String topic, Integer userId) {
        return messageMapper.getNewNoticeMessageCount(topic,userId);
    }

    /**
     * 得到某一个主题发送的最后一条系统通知
     *
     * @param topic  主题
     * @param userId 当前用户id
     * @return 某一个主题发送的最后一条系统通知
     */
    @Override
    public Message getLastNoticeMessage(String topic, Integer userId) {
        return messageMapper.getLastNoticeMessage(topic,userId);
    }

    /**
     * 得到某一个主题的全部系统通知
     *
     * @param topic  主题
     * @param userId 当前用户的id
     * @return 某一个主题的全部系统通知
     */
    @Override
    public List<Message> getMessageByTopic(String topic, Integer userId) {
        return messageMapper.getMessageByTopic(topic, userId);
    }

    @Override
    public int getNewLetterRows(Integer userId, String conversation) {
        return messageMapper.getNewLetter(conversation,userId);
    }
}
