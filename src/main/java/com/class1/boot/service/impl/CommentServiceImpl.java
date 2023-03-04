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

    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filterSensitiveKeyword(comment.getContent()));

        if(comment.getEntityType()==1){
            int count = commentMapper.getCommentCount(comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(),count);
        }

        return commentMapper.insertComment(comment);
    }

    /**
     * 得到用户全部的历史评论和回复
     *
     * @param userId  用户的id
     * @param pageNum 页码
     * @return 分页后的List集合
     */
    @Override
    public PageInfo<Map<String, Object>> getUserAllComment(Integer userId, Integer pageNum) {
        PageHelper.startPage(pageNum,10);
        List<Map<String, Object>> userAllComment = commentMapper.getUserAllComment(userId);
        return new PageInfo<>(userAllComment,5);
    }

    /**
     * 通过id得到评论
     *
     * @param id 评论的id
     * @return 查询得到的评论
     */
    @Override
    public Comment getCommentById(Integer id) {
        return commentMapper.getCommentById(id);
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
