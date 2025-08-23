package com.zicca.zcoupon.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 *
 * @author zicca
 */
@Slf4j
@Service
public class UserService {


    public String getUserInfo() {
        log.info("我的用户信息是：zicca");
        return "zicca";
    }


}
