package com.class1.boot.service.impl;

import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.service.DiscussPostService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    /**
     * 分页显示所有帖子
     */
    @Override
    public PageInfo<Map<String,Object>> getAllDiscussPostByPage(Integer userId, Integer pageNum){
        PageHelper.startPage(pageNum,1);

        List<Map<String,Object>> list = discussPostMapper.getAllDiscussPost(userId);
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(list,5);

        return pageInfo;
    }
}
