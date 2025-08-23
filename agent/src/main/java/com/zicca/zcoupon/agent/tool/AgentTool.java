package com.zicca.zcoupon.agent.tool;

import com.zicca.zcoupon.agent.service.CouponTemplateService;
import com.zicca.zcoupon.agent.service.ReservationReminderService;
import com.zicca.zcoupon.agent.service.UserCouponService;
import com.zicca.zcoupon.agent.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * @author zicca
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentTool {

    private final CouponTemplateService couponTemplateService;
    private final UserCouponService userCouponService;
    private final ReservationReminderService reservationReminderService;
    private final UserService userService;

    @Tool(description = "查看优惠券模板详情")
    public void findCouponTemplate() {
        couponTemplateService.findCouponTemplate();
    }

    @Tool(description = "获取用户领取的优惠券，需要先了解是哪个用户")
    public void getUserCoupon() {
        userCouponService.getUserCoupon();
    }

    @Tool(description = "发送预约提醒，需要先了解是哪个用户")
    public void sendReservationReminder() {
        reservationReminderService.sendReservationReminder();
    }

    @Tool(description = "获取用户信息（当用户询问我的信息时调用该方法）")
    public String getUserInfo() {
        return userService.getUserInfo();
    }


}
