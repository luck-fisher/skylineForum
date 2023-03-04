package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.Message;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {
    /**
     * 得到私信列表
     * @param userId 当前用户的id
     * @return 每个会话的最后一条消息
     */
    @MapKey("message")
    List<Map<String,Object>> getMessageList(Integer userId);

    /**
     * 得到私信的数量
     * @param userId 当前用户的id
     * @return 私信的数量
     */
    int getMessageRows(Integer userId);

    /**
     * 得到某个会话的全部信息
     * @param conversationId 会话id
     * @return 某个会话的全部信息
     */
    List<Message> getAllLetter(String conversationId);

    /**
     * 得到某个会话的全部信息的数量
     * @param conversationId 会话id
     * @return 某个会话的全部信息的数量
     */
    int getLetterRows(String conversationId);

    /**
     * 得到新收到私信的数量
     * @param conversationId 会话id，不传入则总数量
     * @param userId 当前用户的id
     * @return 新收到私信的数量
     */
    int getNewLetter(String conversationId,Integer userId);

    /**
     * 更新私信的状态
     * @param ids 要更新的id集合
     * @param conversationId 要更新的会话id
     * @param status 目标状态
     * @return 更新状态的私信数量
     */
    int updateStatus(List<Integer> ids,String conversationId,Integer status);

    /**
     * 添加私信
     * @param message 要添加的私信
     */
    int addMessage(Message message);

    /**
     * 得到未读系统通知的数量
     * @param topic 主题，未传入则为全部
     * @param userId 当前用户id
     * @return 未读系统通知的数量
     */
    int getNewNoticeMessageCount(String topic,Integer userId);

    /**
     * 得到某个主题系统通知数量
     * @param topic 主题
     * @param userId 当前用户的id
     * @return 某个主题系统通知数量
     */
    int getAllNoticeMessageCount(String topic,Integer userId);

    /**
     * 得到某一个主题发送的最后一条系统通知
     * @param topic 主题
     * @param userId 当前用户id
     * @return 某一个主题发送的最后一条系统通知
     */
    Message getLastNoticeMessage(String topic,Integer userId);

    List<Message> getMessageByTopic(String topic,Integer userId);
}
