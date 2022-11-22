package com.class1.boot.util;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 1;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 2;

    /**
     * 激活失败
     */
    int ACTIVATION_FAIL = 0;

    /**
     * 默认的登录凭证时长
     */
    int DEFAULT_EXPIRED_TIME = 3600*12;

    /**
     * 记住的登录凭证时长
     */
    int REMEMBER_EXPIRED_TIME = 3600*24*100;

    /**
     * 已阅读过的消息
     */
    int READ_LETTER = 0;

    /**
     * 删除的消息
     */
    int DELETE_LETTER = 2;
}
