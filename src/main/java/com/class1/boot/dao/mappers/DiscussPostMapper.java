package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.DiscussPost;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DiscussPostMapper {
    @MapKey("discussPost")
    List<Map<String, Object>> getAllDiscussPost(@Param("userId") Integer userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost getDiscussPostById(Integer id);

    int updateCommentCount(Integer id,Integer commentCount);

    List<DiscussPost> getDiscussPosts();
}
