package com.class1.boot.service.impl;

import com.class1.boot.dao.mappers.CommentMapper;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.Comment;
import com.class1.boot.service.CommentService;
import com.class1.boot.util.SensitiveFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filterSensitiveKeyword(comment.getContent()));

        if(comment.getEntityType()==1){
            int count = commentMapper.getCommentCount(comment.getId());
            discussPostMapper.updateCommentCount(comment.getId(),count);
        }

        return commentMapper.insertComment(comment);
    }

    @Override
    public PageInfo<Map<String, Object>> getAllCommentByPage(Integer pageNum,Integer entityId) {
        PageHelper.startPage(pageNum,10);
        List<Map<String, Object>> commentList = commentMapper.getAllComment(entityId);
        return new PageInfo<>(commentList,5);
    }

    @Override
    public List<Comment> getAllReply(Integer entityId) {
        return commentMapper.getAllReply(entityId);
    }

    @Override
    public int getReplyCount(Integer commentId) {
        return commentMapper.getReplyCount(commentId);
    }
}
