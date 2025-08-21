package com.zicca.zcoupon.engine.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zicca.zcoupon.engine.common.enums.DiscountTargetEnum;
import com.zicca.zcoupon.engine.common.enums.DiscountTypeEnum;
import com.zicca.zcoupon.engine.common.enums.UserCouponStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券实体 | 店铺优惠券
 *
 * @author zicca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_coupon")
public class UserCoupon extends Base {

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
     * 店铺编号
     * 如果是店铺券：存储当前店铺编号
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 领取次数
     */
    @TableField(value = "receive_count")
    private Integer receiveCount;

    /**
     * 领取时间
     */
    @TableField(value = "receive_time")
    private Date receiveTime;

    /**
     * 使用时间
     */
    @TableField(value = "use_time")
    private Date useTime;

    /**
     * 有效期开始时间
     */
    @TableField(value = "valid_start_time")
    private Date validStartTime;

    /**
     * 有效期结束时间
     */
    @TableField(value = "valid_end_time")
    private Date validEndTime;

    /**
     * 优惠券使用状态：
     * 0-未使用 1-锁定 2-已使用 3-已过期 4-已撤回
     */
    @TableField(value = "status")
    private UserCouponStatusEnum status;


}
