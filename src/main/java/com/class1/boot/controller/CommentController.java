package com.class1.boot.controller;

import com.class1.boot.pojo.Comment;
import com.class1.boot.service.CommentService;
import com.class1.boot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add/{postId}")
    public String addComment(Comment comment,@PathVariable("postId") Integer postId){

        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);

        return "redirect:/discussPost/detail/"+postId;
    }
}
