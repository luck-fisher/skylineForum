package com.class1.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MainApplication {

    @PostConstruct
    public void init(){
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }
}
