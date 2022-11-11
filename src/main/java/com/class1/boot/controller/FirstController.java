package com.class1.boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstController {
    @RequestMapping("/hello")
    public String getHello(){
        return "hello! springboot!";
    }
}
