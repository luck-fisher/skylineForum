package com.class1.boot.service.impl;

import com.class1.boot.dao.mappers.UserMapper;
import com.class1.boot.pojo.User;
import com.class1.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(Integer id) {
        User user = userMapper.getUserById(id);
        return user;
    }
}
