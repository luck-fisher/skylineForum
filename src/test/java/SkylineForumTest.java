import cn.hutool.Hutool;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import com.class1.boot.MainApplication;
import com.class1.boot.dao.mappers.DiscussPostMapper;
import com.class1.boot.dao.mappers.LoginTicketMapper;
import com.class1.boot.dao.mappers.UserMapper;
import com.class1.boot.pojo.DiscussPost;
import com.class1.boot.pojo.LoginTicket;
import com.class1.boot.pojo.User;
import com.class1.boot.util.MailClient;
import com.class1.boot.util.SensitiveFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MainApplication.class)
@SpringBootTest
@Slf4j
public class SkylineForumTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testGetUser(){
        User user = userMapper.getUserByEmail("2");
        System.out.println(user);
        log.info("这是正常问题");
    }

    @Test

    public void testInsert(){
        User user = new User();
        user.setUsername("12");
        user.setPassword("12");
        user.setSalt("12");
        user.setEmail("12");
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode("12");
        user.setHeaderUrl("12");
        user.setCreateTime(new Date());

        userMapper.addUser(user);
    }

    @Test
    public void testGetDiscussPost(){
        PageHelper.startPage(1,2);

        List<Map<String,Object>> list = discussPostMapper.getAllDiscussPost(0);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list, 5);

        for (Map<String, Object> discussPost : pageInfo.getList()) {
            User user = userMapper.getUserById((Integer) discussPost.get("user_id"));
            discussPost.put("user",user);
        }

        System.out.println(pageInfo.getList());
    }

    @Test
    public void testMail(){
        mailClient.sendMail("2529704500@qq.com","test","hello");
    }

    @Test
    public void  testMail2(){
        Context context = new Context();
        context.setVariable("username","宝宝");

        String content = templateEngine.process("/demo", context);

        mailClient.sendMail("2055958979@qq.com","hhhh",content);
    }

    @Test
    public void testHuTool(){
//        PageHelper.startPage(1,2);
//        ConverterRegistry converterRegistry = ConverterRegistry.getInstance();
//        List<Map<String, Object>> list = discussPostMapper.getAllDiscussPost(0);
//        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(list,5);
//        for (Map<String,Object> map: pageInfo.getList()) {
//            DiscussPost discussPost = converterRegistry.convert(DiscussPost.class,map);
//            map.clear();
//            map.put("discussPost",discussPost);
//        }
//
//        System.out.println(pageInfo);
//        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(200, 100);
//        circleCaptcha.write("d:/line.png");
        ICaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100);
        System.out.println(captcha.getCode());

    }
    @Test
    public void testLoginTicketMapper(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setStatus(0);
        loginTicket.setTicket("123456789");
        loginTicket.setExpired(new Date());
//        loginTicketMapper.addLoginTicket(loginTicket);
        System.out.println(loginTicketMapper.getByTicket("123456789"));
        loginTicketMapper.updateStatus("123456789",0);
        System.out.println(loginTicketMapper.getByTicket("123456789"));
    }

    @Test
    public void testSensitiveFilter(){
        String text = "傻逼你是！赌博赌博赌博赌博赌博赌博赌博";
        String s = sensitiveFilter.filterSensitiveKeyword(text);
        System.out.println(s);
    }
}
