package com.class1.boot.service;

import com.class1.boot.pojo.LoginTicket;
import com.class1.boot.pojo.User;

import java.util.Map;

public interface UserService {
    /**
     * 根据id得到用户信息
     * @param id 传入的用户id
     * @return 返回查询到的用户信息
     */
    User getUserById(Integer id);

    /**
     * 用户注册信息验证和发送激活邮件
     * @param user 用户输入的信息
     * @return 注册时返回错误信息
     */
    Map<String, Object> doRegister(User user);

    /**
     *
     * @param userId 需要激活的用户id，用于修改用户的status信息
     * @param code 用户的激活码
     * @return 返回激活的结果
     */
    int activation(Integer userId, String code);

    /**
     * 用于用户的登录
     * @param username 用户输入的用户名
     * @param password 用户输入的密码
     * @param expiredSecond 用户的登录凭证时长
     * @return 返回错误信息或者登录凭证
     */
    Map<String,Object> login(String username,String password,int expiredSecond);

    /**
     * 用户退出登录
     * @param ticket 登陆凭证，登出即将status修改为0
     */
    void logout(String ticket);

    /**
     * 得到用户的登录凭证
     * @param ticket 用于查找登录凭证
     * @return 返回登录凭证的完整信息
     */
    LoginTicket getLoginTicket(String ticket);

    /**
     * 修改用户头像
     * @param userId 要修改的用户
     * @param headerUrl 修改后的头像地址
     * @return 更新的行数
     */
    int updateUserHeader(int userId,String headerUrl);

    Map<String,Object> checkPassword(User user,String oldPassword,String newPassword);

    int updatePassword(Integer id, String password);

    User getUserByName(String toName);
}
