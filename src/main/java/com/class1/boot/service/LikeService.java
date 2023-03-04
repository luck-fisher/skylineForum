package com.class1.boot.service;

import org.springframework.stereotype.Service;

/**
 * @author hua'wei
 */
public interface LikeService {
    /**
     * 进行点赞操作
     * @param userId 对当前实体点赞的用户id
     * @param redisLikeKey 当前的实体的redisKey
     * @param entityUserId 当前的实体的的用户id
     */
    void like(Integer userId,String redisLikeKey,Integer entityUserId);

    /**
     * 得到当前实体的点赞数量
     * @param redisLikeKey 当前的实体的redisKey
     * @return 当前实体的点赞数量
     */
    Long getLikeCount(String redisLikeKey);

    /**
     *得到用户得当赞状态
     * @param userId 当前用户
     * @param redisLikeKey 当前的实体的redisKey
     * @return 当前用户的点赞状态
     */
    int getLikeStatus(Integer userId,String redisLikeKey);

    int getUserLikeCount(Integer userId);
}
