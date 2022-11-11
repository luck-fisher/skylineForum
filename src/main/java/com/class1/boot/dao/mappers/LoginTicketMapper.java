package com.class1.boot.dao.mappers;

import com.class1.boot.pojo.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketMapper {
    /**
     * 新建一个登录凭证
     * @param loginTicket
     * @return
     */
    int addLoginTicket(LoginTicket loginTicket);

    /**
     * 通过ticket找到登录凭证
     * @param ticket
     * @return
     */
    LoginTicket getByTicket(String ticket);

    /**
     * 更新当前凭证的状态
     * @param ticket
     * @param status
     * @return
     */
    int updateStatus(String ticket,Integer status);

}
