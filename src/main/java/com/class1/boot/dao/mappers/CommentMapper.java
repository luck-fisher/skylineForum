package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.Comment;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    @MapKey("comment")
    List<Map<String,Object>> getAllComment(Integer entityId);
    List<Comment> getAllReply( Integer entityId);
    int getCommentCount(Integer entityId);
    int getReplyCount(Integer entityId);
    int insertComment(Comment comment);
    @MapKey("comment")
    List<Map<String,Object>> getUserAllComment(Integer userId);
    Comment getCommentById(Integer id);
}
