package com.class1.boot.controller;

import cn.hutool.core.convert.ConverterRegistry;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.User;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    /**
     * 主页的数据显示
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping("/main")
    public String getMain(Model model,Integer pageNum){
        //导入自定义转换器
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        //未传入页码时，默认为第一页
        if(pageNum==null){
            pageNum=1;
        }

        PageInfo<Map<String,Object>> pageInfo = discussPostService.getAllDiscussPostByPage(0,pageNum);
        List<Map<String, Object>> list = pageInfo.getList();

        for (Map<String, Object> map : list) {
            DiscussPost discussPost = converterRegistry.convert(DiscussPost.class,map);
            User user = userService.getUserById(discussPost.getUserId());
            map.clear();
            map.put("discussPost",discussPost);
            map.put("user",user);
        }
        System.out.println(pageInfo);
        model.addAttribute("page",pageInfo);
        return "main";
    }

    @GetMapping("/error")
    public String getError(){
        return "error/500";
    }
}
