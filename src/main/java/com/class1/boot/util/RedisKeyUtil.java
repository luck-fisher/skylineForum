package com.class1.boot.util;

/**
 * @author hua'wei
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_CAPTCHA = "captcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    /**
     * 得到某个实体的赞的redisKey
     * 命名方式为为like:entity:实体类型:实体id
     * 数据类型为 |Key,用户的id| 将点赞的用户id存入set集合
     */
    public static String getLikeRedisKey(Integer entityType,Integer entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    /**
     * 得到用户获得的赞的key
     * 命名方式为user:like:userId
     * 数据结构为 |Key,赞的数量|
     */
    public static String getUserLikeRedisKey(Integer userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    /**
     * 某个实体获得关注
     * 命名方式为follower:实体类型:实体id
     * 数据类型为 |Key，关注的用户的id| 将关注的用户的id和时间存入zSet集合
     */
    public static String getFollowerKey(Integer entityType,Integer entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    /**
     * 某个用户进行关注
     * 命名方式为followee:用户的id:关注的类型
     * 数据类型为 |key，关注的id| 将关注的id存入zSet和时间集合
     */
    public static String getFolloweeKey(Integer userId,Integer entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    /**
     *某个用户登录时的验证码
     * 命名方式为 captcha:随机的id
     * 数据类型为 |key，当前的验证码值| 将验证码存入String类型,有效期为一分钟
     */
    public static String getCaptchaKey(String romId){
        return PREFIX_CAPTCHA+SPLIT+romId;
    }

    /**
     *某个用户登录的登录凭证
     * 命名方式为 ticket:用户的id
     * 数据类型为 |key，用户的登陆凭证| 将登录凭证转换成JSON后存入String类型
     */
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getUserKey(Integer userId){
        return PREFIX_USER+SPLIT+userId;
    }

}
