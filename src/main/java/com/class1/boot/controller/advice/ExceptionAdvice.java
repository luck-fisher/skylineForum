//package com.class1.boot.controller.advice;
//
//import com.class1.boot.util.CommunityUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//@ControllerAdvice(annotations = Controller.class)
//public class ExceptionAdvice {
//    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
//
//    @ExceptionHandler
//    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        logger.error("服务器发生异常");
//        for (StackTraceElement element : e.getStackTrace()) {
//            logger.error(element.toString());
//        }
//        String xRequestWith = request.getHeader("x-request-with");
//        if("XMLHttpRequest".equals(xRequestWith)){
//            response.setContentType("application/plain;charset=utf-8");
//            PrintWriter writer = response.getWriter();
//            writer.write(CommunityUtil.getJsonString(1,"服务器出错啦"));
//        }else {
//            response.sendRedirect(request.getContextPath()+"/error");
//        }
//    }
//}
