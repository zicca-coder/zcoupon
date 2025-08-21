package com.zicca.zcoupon.engine.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 预约提醒实体类
 * @author zicca
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_reservation_reminder")
public class ReservationReminder extends Base{

    /**
     * 用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 优惠券模板 ID
     */
    @TableField(value = "coupon_template_id")
    private Long couponTemplateId;

    /**
     * 预约信息
     */
    @TableField(value = "information")
    private Long information;

    /**
     * 店铺 ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 活动开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

}
