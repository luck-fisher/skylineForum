package com.class1.boot.controller;


import cn.hutool.core.convert.ConverterRegistry;
import com.class1.boot.event.EventConsumer;
import com.class1.boot.event.EventProducer;
import com.class1.boot.pojo.Comment;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.Event;
import com.class1.boot.pojo.User;
import com.class1.boot.service.CommentService;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.service.LikeService;
import com.class1.boot.service.UserService;
import com.class1.boot.service.impl.KafkaProducers;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import com.class1.boot.util.RedisKeyUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author hua'wei
 */
@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String addPost(String title,String content){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(403,"您还没有登录哦！");
        }
        //生成帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setStatus(0);
        post.setType(0);
        post.setCreateTime(new Date());
        discussPostService.insertDiscussPost(post);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_POST)
                .setUserId(user.getId())
                .setEntityId(post.getId())
                .setEntityType(ENTITY_POST);

        EventProducer eventProducer = new EventProducer();
        eventProducer.fireEvent(event);

        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @GetMapping("/detail/{id}")
    public String discussPostDetail(@PathVariable("id") Integer id, Model model,Integer pageNum){
        if(pageNum==null){
            pageNum=1;
        }

        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        //当前帖子
        DiscussPost post = discussPostService.getDiscussPostById(id);
        model.addAttribute("post",post);
        //当前帖子的作者
        User user = userService.getUserById(post.getUserId());
        model.addAttribute("user",user);
        //当前帖子赞数得redisKey
        String redisKey = RedisKeyUtil.getLikeRedisKey(1,post.getId());
        //帖子赞得数量
        Long likeCount = likeService.getLikeCount(redisKey);
        model.addAttribute("likeCount",likeCount);
        //用户对帖子赞的状态
        int likeStatus =hostHolder.getUser()==null? 0 : likeService.getLikeStatus(hostHolder.getUser().getId(), redisKey);
        model.addAttribute("likeStatus",likeStatus);

        //当前帖子所有评论的分页集合
        PageInfo<Map<String, Object>> page = commentService.getAllCommentByPage(pageNum,id);
        for (Map<String,Object> commentVo:page.getList()) {
            Comment comment = converterRegistry.convert(Comment.class, commentVo);
            commentVo.clear();
            //当前评论
            commentVo.put("comment",comment);
            //当前评论的作者
            commentVo.put("user",userService.getUserById(comment.getUserId()));
            //当前评论的redisKey
            redisKey = RedisKeyUtil.getLikeRedisKey(2,comment.getId());
            //评论赞得数量
            likeCount = likeService.getLikeCount(redisKey);
            commentVo.put("likeCount",likeCount);
            //用户对当前评论赞的状态
            likeStatus =hostHolder.getUser()==null? 0 : likeService.getLikeStatus(hostHolder.getUser().getId(), redisKey);
            commentVo.put("likeStatus",likeStatus);
            //当前评论所有回复的相关信息
            List<Map<String,Object>> replyVoList = new ArrayList<>();
            //当前评论的所有回复
            List<Comment> replyList = commentService.getAllReply(comment.getId());
            for (Comment reply: replyList) {
                //当前回复的map集合
                Map<String,Object> replyVo = new HashMap<>(16);
                replyVo.put("reply",reply);
                //当前回复的作者
                replyVo.put("user",userService.getUserById(reply.getUserId()));
                //当前回复的对象不为空时，将回复对象写入当前回复的集合
                User target = reply.getTargetId()==null?null:userService.getUserById(reply.getTargetId());
                replyVo.put("target",target);
                redisKey = RedisKeyUtil.getLikeRedisKey(2,reply.getId());
                //评论赞得数量
                likeCount = likeService.getLikeCount(redisKey);
                replyVo.put("likeCount",likeCount);
                //用户对当前评论赞的状态
                likeStatus =hostHolder.getUser()==null? 0 : likeService.getLikeStatus(hostHolder.getUser().getId(), redisKey);
                replyVo.put("likeStatus",likeStatus);
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

    @GetMapping("/host/{userId}")
    public String getUserAllDiscussPost(@PathVariable("userId") Integer userId,Integer pageNum,Model model){
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        if(pageNum==null){
            pageNum=1;
        }
        PageInfo<Map<String, Object>> pageInfo = discussPostService.getAllDiscussPostByPage(userId, pageNum);
        for (Map<String,Object> map:pageInfo.getList()){
            DiscussPost discussPost = converterRegistry.convert(DiscussPost.class,map);
            String likeRedisKey = RedisKeyUtil.getLikeRedisKey(ENTITY_POST, discussPost.getId());
            Long likeCount = likeService.getLikeCount(likeRedisKey);
            map.clear();
            map.put("post",discussPost);
            map.put("likeCount",likeCount);
        }
        model.addAttribute("page",pageInfo);
        model.addAttribute("userId",userId);
        return "site/my-post";
    }
}
