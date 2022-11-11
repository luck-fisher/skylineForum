package com.class1.boot.controller;

import com.class1.boot.annotation.LoginRequiredAnnotation;
import com.class1.boot.pojo.User;
import com.class1.boot.service.UserService;
import com.class1.boot.util.CommunityUtil;
import com.class1.boot.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${SkylineForum.path.upload}")
    private String upLordPath;

    @Value("${SkylineForum.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequiredAnnotation
    @GetMapping("/setting")
    public String toUserSetting(){
        return "site/setting";
    }

    @LoginRequiredAnnotation
    @PostMapping("/upload")
    public String updateHeader(MultipartFile headerImg,Model model ){
        if(headerImg==null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }
        String fileName = headerImg.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        fileName = CommunityUtil.getId()+suffix;
        File dest = new File(upLordPath+File.separator+fileName);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器出现异常",e);
        }
        String url = domain+contextPath+"/user/header/"+fileName;
        User user = hostHolder.getUser();
        userService.updateUserHeader(user.getId(),url);
        return "redirect:/main";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = upLordPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                ){
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("加载图片失败" + e.getMessage());
        }
    }

    @LoginRequiredAnnotation
    @PostMapping("/updatePassword")
    public String updatePassword(Model model,String oldPassword,String newPassword){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.checkPassword(user, oldPassword,newPassword);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","我们已经给您发送了一封验证邮件，请尽快进行验证");
            model.addAttribute("target","/login");
            return "site/operate-result";
        }else {
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/setting";
        }
    }
    //http:localhost:8080/SkylineForum/user/password/{id}/{加密的password}
    @GetMapping("/password/{id}/{password}")
    public String setPassword(@PathVariable("id") Integer id, @PathVariable("password") String password,Model model){
        int result = userService.updatePassword(id, password);
        if(result==1){
            model.addAttribute("msg","修改密码成功！即将跳转到登录界面");
            model.addAttribute("target","/login");
        } else {
            model.addAttribute("msg","修改密码失败！");
            model.addAttribute("target","/login");
        }
        return "site/operate-result";
    }
}
