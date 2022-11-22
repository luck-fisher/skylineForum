package com.class1.boot.controller;

import cn.hutool.core.convert.ConverterRegistry;
import com.class1.boot.pojo.Message;
import com.class1.boot.pojo.User;
import com.class1.boot.service.MessageService;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityConstant;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/letter")
public class MassageController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public String getMessageList(Model model,Integer pageNum){
        Integer.valueOf("abc");
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
        model.addAttribute("page",page);
        model.addAttribute("allUnReadLetter",newLetterRows);
        return "site/letter";
    }

    @GetMapping("/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,String friendName){
        //得到当前会话的所有私信
        List<Message> letterList = messageService.getAllLetter(conversationId);
        //得到未读消息的id集合
        List<Integer> ids = messageService.getLetterIds(letterList,hostHolder.getUser().getId());
        if(ids!=null){
            int rows = messageService.updateLetterStatus(ids, conversationId, READ_LETTER);
        }
        //将每条消息的发送对象和消息绑定
        List<Map<String,Object>> letterVoList = new ArrayList<>();
        for (Message letter:letterList) {
            Map<String,Object> letterVo = new HashMap<>();
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
}
