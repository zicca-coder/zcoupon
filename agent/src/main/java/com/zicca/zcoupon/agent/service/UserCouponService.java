package com.zicca.zcoupon.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * 用户优惠券服务
 *
 * @author zicca
 */
@Slf4j
@Service
public class UserCouponService {

    public void getUserCoupon() {
        log.info(">>>>>>>>>>>>>获取用户领取的优惠券");
    }


}
