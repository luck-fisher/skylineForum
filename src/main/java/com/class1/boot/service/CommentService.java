package com.class1.boot.service;

import com.class1.boot.pojo.Comment;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface CommentService {
    PageInfo<Map<String,Object>> getAllCommentByPage(Integer pageNum,Integer entityId);

    List<Comment> getAllReply(Integer entityId);

    int getReplyCount(Integer commentId);

    int addComment(Comment comment);
}
