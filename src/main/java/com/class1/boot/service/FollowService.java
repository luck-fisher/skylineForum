package com.class1.boot.service;

/**
 * @author hua'wei
 */
public interface FollowService {
    /**
     * 关注事件
     * @param userId 当前进行关注操作的用户id
     * @param entityType 当前被关注的实体类型
     * @param entityId 当前被关注的实体id
     */
    void follow(Integer userId,Integer entityType,Integer entityId);

    /**
     * 取消关注事件
     * @param userId 当前取消关注的用户id
     * @param entityType 当前被取消关注的实体类型
     * @param entityId 当前被取消关注的实体id
     */
    void unFollow(Integer userId,Integer entityType,Integer entityId);

    /**
     * 查询当前实体的关注者的数量
     * @param entityType 当前实体的类型
     * @param entityId 当前实体的id
     * @return 当前实体的关注者的数量
     */
    Long getFollowerCount(Integer entityType,Integer entityId);

    /**
     * 查询当前用户的关注数量
     * @param userId 当前用户的id
     * @param entityType 用户关注的类型
     * @return 当前用户的关注数量
     */
    Long getFolloweeCount(Integer userId,Integer entityType);

    /**
     * 查询当前用户的关注状态
     * @param userId 当前用户的id
     * @param entityType 当前的实体对象类型
     * @param entityId 当前实体对象的id
     * @return 当前用户的关注状态
     */
    Boolean getFollowStatus(Integer userId,Integer entityType,Integer entityId);
}
