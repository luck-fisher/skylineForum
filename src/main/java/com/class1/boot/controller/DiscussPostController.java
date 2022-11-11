package com.class1.boot.controller;


import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.User;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/{pageNum}")
    public String showMain(Model model, @PathVariable("pageNum") Integer pageNum){
        PageInfo<Map<String,Object>> pageInfo = discussPostService.getAllDiscussPostByPage(0,pageNum);
        pageInfo.getList();

        for (Map<String, Object> discussPost : pageInfo.getList()) {

            User user = userService.getUserById((Integer) discussPost.get("user_id"));
            discussPost.put("user",user);

        }
        model.addAttribute("page",pageInfo);
        return "main";
    }
}
