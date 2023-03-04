package com.class1.boot.service.impl;

import com.class1.boot.service.FollowService;
import com.class1.boot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author hua'wei
 */
@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 关注事件
     *
     * @param userId     当前进行关注操作的用户id
     * @param entityType 当前被关注的实体类型
     * @param entityId   当前被关注的实体id
     */
    @Override
    public void follow(Integer userId, Integer entityType, Integer entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    /**
     * 查询当前用户的关注状态
     *
     * @param userId     当前用户的id
     * @param entityType 当前的实体对象类型
     * @param entityId   当前实体对象的id
     * @return 当前用户的关注状态
     */
    @Override
    public Boolean getFollowStatus(Integer userId, Integer entityType, Integer entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    /**
     * 查询当前实体的关注者的数量
     *
     * @param entityType 当前实体的类型
     * @param entityId   当前实体的id
     * @return 当前实体的关注者的数量
     */
    @Override
    public Long getFollowerCount(Integer entityType, Integer entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 查询当前用户的关注数量
     *
     * @param userId     当前用户的id
     * @param entityType 用户关注的类型
     * @return 当前用户的关注数量
     */
    @Override
    public Long getFolloweeCount(Integer userId, Integer entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 取消关注事件
     *
     * @param userId     当前取消关注的用户id
     * @param entityType 当前被取消关注的实体类型
     * @param entityId   当前被取消关注的实体id
     */
    @Override
    public void unFollow(Integer userId, Integer entityType, Integer entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);
                return operations.exec();
            }
        });
    }
}
