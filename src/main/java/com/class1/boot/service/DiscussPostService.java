package com.class1.boot.service;

import com.class1.boot.pojo.DiscussPost;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface DiscussPostService {
    PageInfo<Map<String,Object>> getAllDiscussPostByPage(Integer userId, Integer pageNum);
}
