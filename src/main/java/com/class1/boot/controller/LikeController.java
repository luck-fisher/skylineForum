package com.class1.boot.controller;

import com.class1.boot.event.EventProducer;
import com.class1.boot.pojo.Event;
import com.class1.boot.pojo.User;
import com.class1.boot.service.LikeService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import com.class1.boot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/like")
    @ResponseBody
    public String like(Integer entityType,Integer entityId,Integer entityUserId,Integer postId){
        User user = hostHolder.getUser();
        String redisLikeKey = RedisKeyUtil.getLikeRedisKey(entityType,entityId);

        likeService.like(user.getId(),redisLikeKey,entityUserId);
        Long likeCount = likeService.getLikeCount(redisLikeKey);
        int likeStatus = likeService.getLikeStatus(user.getId(), redisLikeKey);

        Map<String,Object> map = new HashMap<>(16);
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        if(likeStatus == 1){
            Event event = new Event();
            event.setUserId(hostHolder.getUser().getId())
                    .setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityCreateId(entityUserId)
                    .setDate("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0,null,map);
    }
}
