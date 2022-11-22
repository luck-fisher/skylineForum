package com.class1.boot.service;

import com.class1.boot.pojo.Message;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface MessageService {
    /**
     * 得到用户所有的会话
     * @param userId 当前用户的id
     * @return 当前用户所有的会话的最新一条消息
     */
    PageInfo<Map<String,Object>> getAllMessage(Integer userId, Integer pageNum);

    /**
     * 得到会话的行数
     * @param userId 当前用户的id
     * @return 返回会话的行数
     */
    int getMessageRows(Integer userId);

    /**
     * 得到当前会话的消息记录
     * @param conversationId 当前会话的id
     * @return 当前会话所有的消息记录
     */
    List<Message> getAllLetter(String conversationId);

    /**
     * 得到当前会话的消息行数
     * @param conversationId 当前会话的id
     * @return 当前会话的消息行数
     */
    int getLetterRows(String conversationId);

    /**
     * 得到用户未读信息的数量
     * @param userId 当前用户
     * @param conversation 指定的会话，不指定则为null，查询所有的未读信息行数
     * @return 用户未读信息的数量
     */
    int getNewLetterRows(Integer userId,String conversation);

    /**
     * 更新消息的状态
     * @param ids 需要更改的消息id，为null则更新当前会话的全部消息
     * @param conversationId 当前会话的id，为null则更新所有会话的所有消息
     * @param status 目标状态，0-已读，1-未读，2-删除
     * @return 返回更新的行数
     */
    int updateLetterStatus(List<Integer> ids,String conversationId,Integer status);

    /**
     * 得到要更新的id
     * @param letterList 当前会话的所有消息
     * @param userId 打开当前会话的用户id
     * @return 要更新的id
     */
    List<Integer> getLetterIds(List<Message> letterList,Integer userId);

    /**
     * 添加消息
     * @param message 当前发送的消息
     * @return 添加的数量
     */
    int addMessage(Message message);
}
