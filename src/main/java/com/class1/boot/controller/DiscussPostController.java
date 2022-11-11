package com.class1.boot.controller;


import cn.hutool.core.convert.ConverterRegistry;
import com.class1.boot.pojo.Comment;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.User;
import com.class1.boot.service.CommentService;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController{
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    public String addPost(String title,String content){
        System.out.println(title);
        System.out.println(content);
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(403,"您还没有登录哦！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setStatus(0);
        post.setType(0);
        post.setCreateTime(new Date());
        discussPostService.insertDiscussPost(post);

        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @GetMapping("/detail/{id}")
    public String discussPostDetail(@PathVariable("id") Integer id, Model model,Integer pageNum){
        if(pageNum==null){
            pageNum=1;
        }
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        DiscussPost post = discussPostService.getDiscussPostById(id);
        //当前帖子
        model.addAttribute("post",post);
        //当前帖子的作者
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user",user);
        //当前帖子所有评论的分页集合
        PageInfo<Map<String, Object>> page = commentService.getAllCommentByPage(pageNum,id);
        for (Map<String,Object> commentVo:page.getList()) {
            Comment comment = converterRegistry.convert(Comment.class, commentVo);
            commentVo.clear();
            //当前评论
            commentVo.put("comment",comment);
            //当前评论的作者
            commentVo.put("user",userService.getUserById(comment.getUserId()));
            //当前评论所有回复的相关信息
            List<Map<String,Object>> replyVoList = new ArrayList<>();
            //当前评论的所有回复
            List<Comment> replyList = commentService.getAllReply(comment.getId());
            for (Comment reply: replyList) {
                //当前回复的map集合
                Map<String,Object> replyVo = new HashMap<>();
                replyVo.put("reply",reply);
                //当前回复的作者
                replyVo.put("user",userService.getUserById(reply.getUserId()));
                //当前回复的对象不为空时，将回复对象写入当前回复的集合
                User target = reply.getTargetId()==null?null:userService.getUserById(reply.getTargetId());
                replyVo.put("target",target);
                //将当前回复装入每条评论的回复集合
                replyVoList.add(replyVo);
            }
            //将所有回复装入当前评论的回复集合
            commentVo.put("replyVoList",replyVoList);
            commentVo.put("replyCount",commentService.getReplyCount(comment.getId()));
        }
        model.addAttribute("page",page);
        return "site/discuss-detail";
    }
}
