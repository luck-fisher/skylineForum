package com.class1.boot.controller;

import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.User;
import com.class1.boot.service.DiscussPostService;
import com.class1.boot.service.ElasticService;
import com.class1.boot.service.LikeService;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.HostHolder;
import com.class1.boot.util.RedisKeyUtil;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ElasticSearchController implements CommunityConstant {

    @Resource
    private ElasticService elasticService;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private LikeService likeService;
    @Resource
    private UserService userService;
    @Resource
    private HostHolder hostHolder;

    @GetMapping("/search")
    public String searchPost(Integer pageNum,String keyword,Model model){
        if (pageNum == null){
            pageNum = 0;
        }
        //1.查找到搜索的帖
        SearchHits<DiscussPost> searchRes = elasticService.getSearchRes(keyword, pageNum);

        //2.将帖子的相关信息，如：user，post，like数聚合
        List<Map<String,Object>> resList = new ArrayList<>();
        for (SearchHit<DiscussPost> hit:searchRes){
            HashMap<String, Object> resMap = new HashMap<>();

            DiscussPost post = hit.getContent();
            resMap.put("post",post);

            User user = userService.getUserById(post.getUserId());
            resMap.put("user",user);

            String likeRedisKey = RedisKeyUtil.getLikeRedisKey(ENTITY_POST, post.getId());
            Long likeCount = likeService.getLikeCount(likeRedisKey);
            int likeStatus = likeService.getLikeStatus(hostHolder.getUser().getId(), likeRedisKey);
            resMap.put("likeCount",likeCount);
            resMap.put("likeStatus",likeStatus);

            resList.add(resMap);
        }
        model.addAttribute("resList",resList);
        model.addAttribute("keyword",keyword);
        return "site/search";
    }
}
