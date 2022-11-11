package com.class1.boot.service.impl;

import cn.hutool.core.convert.Convert;
import com.class1.boot.dao.mappers.LoginTicketMapper;
import com.class1.boot.dao.mappers.UserMapper;
import com.class1.boot.pojo.LoginTicket;
import com.class1.boot.pojo.User;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Value("${SkylineForum.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Override
    public Map<String, Object> doRegister(User user) {
        Map<String, Object> map = new HashMap<>();
        //判断注册属性的填写
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }

        //判断用户是否已被注册
        User u = userMapper.getUserByUsername(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","用户名已被注册!");
            return map;
        }
        u = userMapper.getUserByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已被注册!");
            return map;
        }

        //设置用户初始属性
        user.setSalt(CommunityUtil.getId().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setCreateTime(new Date());
        user.setStatus(0);
        user.setType(0);
        user.setActivationCode(CommunityUtil.getId());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));

        //创建新用户
        userMapper.addUser(user);
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http:localhost:8080/SkylineForum/activation/{id}/{code}
        String url = domain + contextPath + "/activation/" +user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String process = templateEngine.process("/demo", context);
        mailClient.sendMail(user.getEmail(),"天际社区账号激活",process);

        return map;
    }

    @Override
    public int activation(Integer userId, String code) {
        User user = userMapper.getUserById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAIL;
        }
    }

    @Override
    public int updatePassword(Integer id, String password) {
        return userMapper.updatePassword(id,password);
    }

    @Override
    public Map<String, Object> checkPassword(User user, String oldPassword,String newPassword) {
        Map<String,Object> map = new HashMap<>();
        //对密码进行判定
        if(newPassword.length()<8){
            map.put("passwordLengthMsg","密码长度不能小于8！");
            return map;
        }
        if(!user.getPassword().equals(CommunityUtil.md5(oldPassword+user.getSalt()))){
            map.put("passwordMsg","原密码错误！");
            return map;
        }
        if(oldPassword.equals(newPassword)){
            map.put("passwordMsg","新密码不能与原密码相同！");
            return map;
        }
        //向用户发送验证邮件
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        String password = CommunityUtil.md5(newPassword+user.getSalt());
        //http:localhost:8080/SkylineForum/user/password/{id}/{加密的password}
        String url = domain + contextPath + "/user/password/" + user.getId() + "/" + password;
        context.setVariable("url",url);
        String process = templateEngine.process("/password_update", context);
        mailClient.sendMail(user.getEmail(),"天际社区修改密码验证",process);
        return map;
    }

    @Override
    public int updateUserHeader(int userId, String headerUrl) {
        return userMapper.updateHeaderUrl(userId,headerUrl);
    }

    @Override
    public Map<String, Object> login(String username,String password,int expiredSecond) {
        Map<String, Object> map = new HashMap<>();
        //输入验证
        if(username==null){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if (password==null){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //信息验证
        User user = userMapper.getUserByUsername(username);
        if(user==null){
            map.put("usernameMsg","该用户不存在");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","该用户还未激活");
            return map;
        }
        password = CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(1);
        loginTicket.setTicket(CommunityUtil.getId());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecond* 1000L));
        loginTicketMapper.addLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,0);
    }

    @Override
    public LoginTicket getLoginTicket(String ticket) {
        return loginTicketMapper.getByTicket(ticket);
    }
}
