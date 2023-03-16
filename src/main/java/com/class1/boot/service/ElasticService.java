package com.class1.boot.service;

import com.class1.boot.pojo.DiscussPost;
import com.github.pagehelper.Page;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Map;

public interface ElasticService {
    /**
     *把帖子保存在elastic中
     */
    void savePost(DiscussPost post);

    /**
     *删除帖子
     */
    void deletePost(int postId);

    /**
     *获得查询的分页集合
     */
    SearchHits<DiscussPost> getSearchRes(String keyword, int pageNum);
}
