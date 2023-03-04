package com.class1.boot.service;

import com.class1.boot.pojo.Comment;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface CommentService {
    /**
     * 获取当前帖子的所有评论
     * @param pageNum 页数
     * @param entityId 当前帖子的id
     * @return 返回分页后帖子的评论集合
     */
    PageInfo<Map<String,Object>> getAllCommentByPage(Integer pageNum,Integer entityId);

    /**
     * 返回当前评论的所有回复
     * @param entityId 当前评论的回复
     * @return 回复集合
     */
    List<Comment> getAllReply(Integer entityId);

    /**
     * 获取当前评论的回复条数
     * @param commentId 当前评论的id
     * @return 回复条数
     */
    int getReplyCount(Integer commentId);

    /**
     * 添加新的评论或者回复
     * @param comment 当前添加的对象
     * @return 添加的条数
     */
    int addComment(Comment comment);

    /**
     * 得到用户全部的历史评论和回复
     * @param userId 用户的id
     * @param pageNum 页码
     * @return 分页后的List集合
     */
    PageInfo<Map<String,Object>> getUserAllComment(Integer userId,Integer pageNum);

    /**
     * 通过id得到评论
     * @param id 评论的id
     * @return 查询得到的评论
     */
    Comment getCommentById(Integer id);
}
