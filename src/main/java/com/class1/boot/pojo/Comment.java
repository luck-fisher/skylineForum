package com.class1.boot.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private Integer targetId;
    private Integer status;
    private Date createTime;

    private String content;

}
