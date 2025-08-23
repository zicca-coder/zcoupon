package com.zicca.zcoupon.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * 预约提醒服务
 *
 * @author zicca
 */
@Slf4j
@Service
public class ReservationReminderService {

    public void sendReservationReminder() {
        log.info("发送预约提醒");
    }

}
