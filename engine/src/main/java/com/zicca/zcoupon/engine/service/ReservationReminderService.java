package com.zicca.zcoupon.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zicca.zcoupon.engine.dao.entity.ReservationReminder;
import com.zicca.zcoupon.engine.dto.req.ReservationReminderCancelReqDTO;
import com.zicca.zcoupon.engine.dto.req.ReservationReminderCreateReqDTO;

/**
 * 预约提醒服务
 *
 * @author zicca
 */
public interface ReservationReminderService extends IService<ReservationReminder> {


    /**
     * 创建预约提醒
     *
     * @param requestParam 请求参数
     */
    void createReservationReminder(ReservationReminderCreateReqDTO requestParam);


    /**
     * 取消预约提醒
     *
     * @param requestParam 请求参数
     */
    void cancelReservationReminder(ReservationReminderCancelReqDTO requestParam);

    boolean isCancelRemind();

    void listReservationReminder(Long userId);

}
