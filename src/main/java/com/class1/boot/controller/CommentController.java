package com.class1.boot.controller;

import com.class1.boot.annotation.LoginRequiredAnnotation;
import com.class1.boot.event.EventProducer;
import com.class1.boot.pojo.Comment;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.Event;
import com.class1.boot.service.CommentService;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;

    @PostMapping("/add/{postId}")
    @LoginRequiredAnnotation
    public String addComment(Comment comment,@PathVariable("postId") Integer postId){
        //设置评论的基本信息
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event();
        event.setUserId(hostHolder.getUser().getId())
                .setTopic(TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setDate("postId",postId);
        //判断对象类型，设置event的对象的createUserId属性,即设置系统通知的接受者
        if (comment.getEntityType()==ENTITY_POST){
            DiscussPost target = discussPostService.getDiscussPostById(comment.getEntityId());
            event.setEntityCreateId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_COMMENT){
            Comment target = commentService.getCommentById(comment.getEntityId());
            event.setEntityCreateId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        //触发更新帖子事件
         event = new Event()
                .setTopic(TOPIC_POST)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(postId)
                .setEntityType(ENTITY_POST);

        eventProducer.fireEvent(event);

        return "redirect:/discussPost/detail/"+postId;
    }
}
