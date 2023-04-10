package com.class1.boot.service.impl;

import com.class1.boot.controller.LoginController;
import com.class1.boot.pojo.LoginUser;
import com.class1.boot.pojo.User;
import com.class1.boot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //判断用户是否存在
        User user = userService.getUserByName(username);
        if (user == null){
            logger.error("未找到用户！");
        }
        //获取用户权限信息


        return new LoginUser(new HashSet<>(),user);
    }
}
