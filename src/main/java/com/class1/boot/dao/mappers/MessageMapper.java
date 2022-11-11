package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.Message;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {
    @MapKey("message")
    List<Map<String,Object>> getAllMessage(Integer userId);

    int getMessageRows(Integer userId);

    List<Message> getAllLetter(String conversationId);

    int getLetterRows(String conversationId,Integer status);


}
