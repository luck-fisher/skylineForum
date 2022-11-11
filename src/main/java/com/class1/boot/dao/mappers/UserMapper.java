package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserById(Integer Id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    void addUser(User user);
}
