package com.class1.boot.service.impl;

import com.class1.boot.service.LikeService;
import com.class1.boot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 进行点赞操作
     * @param userId       对当前实体点赞的用户id
     * @param redisLikeKey 当前的实体的redisKey
     * @param entityUserId 当前的实体的用户的id
     */
    @Override
    public void like(Integer userId, String redisLikeKey,Integer entityUserId) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String userRedisKey = RedisKeyUtil.getUserLikeRedisKey(entityUserId);
                Boolean isMember = operations.opsForSet().isMember(redisLikeKey, userId);
                operations.multi();
                if(Boolean.TRUE.equals(isMember)){
                    operations.opsForSet().remove(redisLikeKey,userId);
                    operations.opsForValue().decrement(userRedisKey);
                }else {
                    operations.opsForSet().add(redisLikeKey,userId);
                    operations.opsForValue().increment(userRedisKey);
                }
                return operations.exec();
            }
        });
    }

    /**
     * 得到当前实体的点赞数量
     * @param redisLikeKey 当前的实体的redisKey
     * @return 当前实体的点赞数量
     */
    @Override
    public Long getLikeCount(String redisLikeKey) {
        return redisTemplate.opsForSet().size(redisLikeKey);
    }

    /**
     * 得到用户得当赞状态
     * @param userId       当前用户
     * @param redisLikeKey 当前的实体的redisKey
     * @return 当前用户的点赞状态
     */
    @Override
    public int getLikeStatus(Integer userId, String redisLikeKey) {
       return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(redisLikeKey, userId)) ?1:0;
    }

    @Override
    public int getUserLikeCount(Integer userId) {
        String userRedisKey = RedisKeyUtil.getUserLikeRedisKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userRedisKey);
        return count==null?0:count;
    }
}
