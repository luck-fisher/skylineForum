package com.class1.boot.controller;

import cn.hutool.core.convert.ConverterRegistry;
import com.alibaba.fastjson2.JSONObject;
import com.class1.boot.pojo.Message;
import com.class1.boot.pojo.User;
import com.class1.boot.service.MessageService;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/letter")
public class MassageController implements CommunityConstant {

    @Resource
    private HostHolder hostHolder;

    @Resource
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public String getMessageList(Model model,Integer pageNum){
        if(pageNum==null){
            pageNum=1;
        }
        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
        User user = hostHolder.getUser();
        PageInfo<Map<String, Object>> page = messageService.getAllMessage(user.getId(), pageNum);
        for (Map<String,Object> messageMap:page.getList()) {
            Message message = converterRegistry.convert(Message.class, messageMap);
            String conversationId = message.getConversationId();
            //将会话的消息和条数以及未读条数存入会话map
            messageMap.clear();
            messageMap.put("message",message);
            messageMap.put("unReadLetter",messageService.getNewLetterRows(user.getId(),conversationId));
            messageMap.put("letterRows",messageService.getLetterRows(conversationId));
            //将会话的对象存入map集合
            int targetId = Objects.equals(user.getId(), message.getFromId()) ?message.getToId():message.getFromId();
            messageMap.put("target",userService.getUserById(targetId));

        }
        int newLetterRows = messageService.getNewLetterRows(user.getId(), null);
        int newNoticeMessageCount = messageService.getNewNoticeMessageCount(null, user.getId());
        model.addAttribute("page",page);
        model.addAttribute("allUnReadLetter",newLetterRows);
        model.addAttribute("newNoticeRows",newNoticeMessageCount);
        return "site/letter";
    }

    @GetMapping("/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,String friendName){
        //得到当前会话的所有私信
        List<Message> letterList = messageService.getAllLetter(conversationId);
        //得到未读消息的id集合
        List<Integer> ids = messageService.getLetterIds(letterList,hostHolder.getUser().getId());
        String nullList = "[]";
        if(!nullList.equals(ids.toString())){
            int rows = messageService.updateLetterStatus(ids, conversationId, READ_LETTER);
        }
        //将每条消息的发送对象和消息绑定
        List<Map<String,Object>> letterVoList = new ArrayList<>();
        for (Message letter:letterList) {
            Map<String,Object> letterVo = new HashMap<>(16);
            letterVo.put("letter",letter);
            User target = userService.getUserById(letter.getFromId());
            letterVo.put("target",target);
            letterVoList.add(letterVo);
        }
        model.addAttribute("letterList",letterVoList);
        model.addAttribute("friendName",friendName);
        return "site/letter-detail";
    }

    @PostMapping("/sendLetter")
    @ResponseBody
    public String sendLetter(String toName,String content){
        User user = hostHolder.getUser();
        User target = userService.getUserByName(toName);
        if (target==null){
            return CommunityUtil.getJsonString(1,"目标用户不存在哦");
        }
        //补充消息信息
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(target.getId());
        message.setContent(content);
        message.setStatus(1);
        message.setCreateTime(new Date());
        if(user.getId()<target.getId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }

        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeLetterList(Model model){
        Map<String,Object> noticeMessageVo = new HashMap<>(16);
        Integer userId = hostHolder.getUser().getId();
        //得到朋友私信的总未读消息
        int newLetterRows = messageService.getNewLetterRows(userId, null);
        model.addAttribute("newLetterRows",newLetterRows);
        //得到系统通知的总未读消息
        int newNoticeMessageCount = messageService.getNewNoticeMessageCount(null, userId);
        model.addAttribute("newNoticeRows",newNoticeMessageCount);
        //得到评论主题通知的的各种信息
        int commentNoticeCount = messageService.getNoticeMessageCount(TOPIC_COMMENT, userId);
        int newCommentNoticeCount = messageService.getNewNoticeMessageCount(TOPIC_COMMENT, userId);
        Message commentNoticeLastMessage = messageService.getLastNoticeMessage(TOPIC_COMMENT, userId);
        Map<String,Object> commentNoticeContent = JSONObject.parseObject(commentNoticeLastMessage.getContent(),HashMap.class);
        User commentElement = userService.getUserById((Integer) commentNoticeContent.get("userId"));
        model.addAttribute("commentNoticeRows",commentNoticeCount);
        model.addAttribute("newCommentNoticeRows",newCommentNoticeCount);
        model.addAttribute("commentNoticeLastTime",commentNoticeLastMessage.getCreateTime());
        model.addAttribute("commentNoticeContent",commentNoticeContent);
        model.addAttribute("commentElement",commentElement);
        //得到赞主题通知的各种信息
        int likeNoticeCount = messageService.getNoticeMessageCount(TOPIC_LIKE, userId);
        int newLikeNoticeCount = messageService.getNewNoticeMessageCount(TOPIC_LIKE, userId);
        Message likeNoticeLastMessage = messageService.getLastNoticeMessage(TOPIC_LIKE, userId);
        Map<String,Object> likeNoticeContent= JSONObject.parseObject(likeNoticeLastMessage.getContent(),HashMap.class);
        User likeElement = userService.getUserById((Integer) likeNoticeContent.get("userId"));
        model.addAttribute("likeNoticeRows",likeNoticeCount);
        model.addAttribute("newLikeNoticeRows",newLikeNoticeCount);
        model.addAttribute("likeNoticeLastTime",likeNoticeLastMessage.getCreateTime());
        model.addAttribute("likeNoticeContent",likeNoticeContent);
        model.addAttribute("likeElement",likeElement);
        //得到关注主题通知的各种信息
        int followNoticeCount = messageService.getNoticeMessageCount(TOPIC_FOLLOW, userId);
        int newFollowNoticeCount = messageService.getNewNoticeMessageCount(TOPIC_FOLLOW, userId);
        Message followNoticeLastMessage = messageService.getLastNoticeMessage(TOPIC_FOLLOW, userId);
        Map<String,Object> followNoticeContent= JSONObject.parseObject(followNoticeLastMessage.getContent(),HashMap.class);
        User followElement = userService.getUserById((Integer) followNoticeContent.get("userId"));
        model.addAttribute("followNoticeRows",followNoticeCount);
        model.addAttribute("newFollowNoticeRows",newFollowNoticeCount);
        model.addAttribute("followNoticeLastTime",followNoticeLastMessage.getCreateTime());
        model.addAttribute("followNoticeContent",followNoticeContent);
        model.addAttribute("followElement",followElement);
        return "site/notice";
    }
    @GetMapping("/notice/detail")
    public String getNoticeDetail(String topic,Model model){
        List<Map<String,Object>> noticeVo = new ArrayList<>();
        List<Message> messageList = messageService.getMessageByTopic(topic, hostHolder.getUser().getId());
        for (Message message:messageList) {
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String,Object> messageContent = JSONObject.parseObject(content,HashMap.class);
            messageContent.put("element",userService.getUserById((Integer) messageContent.get("userId")));
            messageContent.put("eventTime",message.getCreateTime());
            noticeVo.add(messageContent);
        }
        List<Integer> ids = messageService.getLetterIds(messageList, hostHolder.getUser().getId());
        String nullList = "[]";
        if(!nullList.equals(ids.toString())){
            int rows = messageService.updateLetterStatus(ids, topic, READ_LETTER);
        }
        model.addAttribute("noticeVo",noticeVo);
        model.addAttribute("topic",topic);
        return "site/notice-detail";
    }
}
