package com.class1.boot.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import com.class1.boot.pojo.User;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Value("server.servlet.context-path")
    private String contextPath;

    @GetMapping("/login")
    public String toLogin(){
        return "site/login";
    }

    @GetMapping ("/register")
    public String toRegister(){
        return "site/register";
    }

    @PostMapping("/register")
    public String doRegister(Model model,User user){
        Map<String, Object> map = userService.doRegister(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","我们已经给您发送了一封激活邮件，请尽快进行激活");
            model.addAttribute("target","/");
            return "site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("map",map);
        }
        return "site/register";
    }
    //http://localhost:8080/SkylineForum/activation/18/c905c1d3642d4801bf1e1af5094f16b4
    //http://localhost:8080/SkylineForum/activation/{id}/{code}
    @GetMapping("/activation/{id}/{code}")
    public String activation(@PathVariable("code") String code, @PathVariable("id") Integer id,Model model){
        int result = userService.activation(id, code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","您已成功激活！");
            model.addAttribute("target","/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg","请勿重复激活！");
            model.addAttribute("target","/main");
        }else {
            model.addAttribute("msg","激活失败！");
            model.addAttribute("target","/main");
        }
        return "site/operate-result";
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session) {
        ICaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100);
        session.setAttribute("captcha",captcha.getCode());
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            captcha.write(os);
            os.close();
        } catch (IOException e) {
            logger.error("响应验证码失败："+e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username,String password,Boolean rememberme,String code,HttpSession session,
                        Model model,HttpServletResponse response){
        //判断验证码是否正确
        String captcha = session.getAttribute("captcha").toString();
        if(captcha.isBlank()||code.isBlank()||!captcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "site/login";
        }
        //判断账号和密码是否正确
        int expiredSecond = rememberme!=null?REMEMBER_EXPIRED_TIME:DEFAULT_EXPIRED_TIME;
        Map<String, Object> map = userService.login(username, password, expiredSecond);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/main";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
