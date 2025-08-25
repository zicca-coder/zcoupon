package com.zicca.zcoupon.engine.toolkit;

import cn.hutool.core.date.DateUtil;
import com.zicca.zcoupon.engine.common.enums.ReminderTypeEnum;
import com.zicca.zcoupon.engine.dto.req.ReservationReminderQueryRespDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 预约提醒工具类
 *
 * @author zicca
 */
public final class ReservationReminderUtil {

    /**
     * 下一个类型的位移量，每个类型占用12个bit位，共计60分钟
     */
    private static final int NEXT_TYPE_BITS = 12;

    /**
     * 5分钟为一个间隔
     */
    private static final int TIME_INTERVAL = 5;

    /**
     * 提醒方式的数量
     */
    private static final int TYPE_COUNT = ReminderTypeEnum.values().length;

    /**
     * 填充预约信息
     */
    public static void fillRemindInformation(ReservationReminderQueryRespDTO resp, Long information) {

    }


}
