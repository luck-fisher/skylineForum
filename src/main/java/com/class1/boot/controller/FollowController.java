package com.class1.boot.controller;

import com.class1.boot.annotation.LoginRequiredAnnotation;
import com.class1.boot.event.EventProducer;
import com.class1.boot.pojo.Event;
import com.class1.boot.pojo.User;
import com.class1.boot.service.FollowService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hua'wei
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping ("/follow")
    @ResponseBody
    @LoginRequiredAnnotation
    public String follow(Integer entityType, Integer entityId){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJsonString(1,"请先登录");
        }
        followService.follow(user.getId(),entityType,entityId);
        Event event = new Event();
        event.setUserId(hostHolder.getUser().getId())
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityCreateId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJsonString(0,"已关注");
    }

    @PostMapping ("/unfollow")
    @ResponseBody
    @LoginRequiredAnnotation
    public String unfollow(Integer entityType, Integer entityId){
        User user = hostHolder.getUser();

        followService.unFollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"取关成功");
    }
}
