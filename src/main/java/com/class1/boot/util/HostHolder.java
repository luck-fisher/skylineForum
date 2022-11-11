package com.class1.boot.util;

import com.class1.boot.pojo.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替Session的作用，用于处理多并发
 */
@Component
public class HostHolder {
    ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
