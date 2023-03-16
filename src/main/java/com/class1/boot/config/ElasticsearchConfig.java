//package com.class1.boot.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//import org.springframework.stereotype.Component;
//
//@ConfigurationProperties(prefix = "elasticsearch")
//@Component
//@Configuration(proxyBeanMethods = false)
//public class ElasticsearchConfig extends ElasticsearchConfiguration {
//
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//    }
//
//}
