package com.class1.boot.service.impl;

import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.util.SensitiveFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    /**
     * 分页显示所有帖子
     */
    @Override
    public PageInfo<Map<String,Object>> getAllDiscussPostByPage(Integer userId, Integer pageNum){
        PageHelper.startPage(pageNum,10);

        List<Map<String,Object>> list = discussPostMapper.getAllDiscussPost(userId);

        return new PageInfo<>(list,5);
    }

    public int insertDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("内容不能为空！");
        }

        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filterSensitiveKeyword(post.getTitle()));
        post.setContent(sensitiveFilter.filterSensitiveKeyword(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost getDiscussPostById(Integer id) {
        return discussPostMapper.getDiscussPostById(id);
    }
}
