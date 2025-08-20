package com.zicca.zcoupon.merchant.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zicca.zcoupon.merchant.admin.common.enums.CouponStatusEnum;
import com.zicca.zcoupon.merchant.admin.common.enums.DiscountTargetEnum;
import com.zicca.zcoupon.merchant.admin.common.enums.DiscountTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺优惠券模板实体类
 *
 * @author zicca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_coupon_template")
public class CouponTemplate extends Base {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 店铺编号
     * 如果是店铺券：存储当前店铺编号
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 优惠对象:
     * 0：指定商品可用，1：全店通用
     */
    @TableField(value = "target")
    private DiscountTargetEnum target;

    /**
     * 优惠类型：
     * 0：立减券 1：满减券 2：折扣券 3：随机券
     */
    @TableField(value = "type")
    private DiscountTypeEnum type;

    /**
     * 面值（立减金额/折扣率/随机最小值等）
     */
    @TableField(value = "face_value")
    private BigDecimal faceValue;

    /**
     * 满减券门槛金额（仅满减券有效）
     */
    @TableField(value = "min_amount")
    private BigDecimal minAmount;

    /**
     * 折扣券最多优惠金额（仅折扣券有效）
     */
    @TableField(value = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    /**
     * 随机券最大金额（仅随机券有效）
     */
    @TableField(value = "max_random_amount")
    private BigDecimal maxRandomAmount;

    /**
     * 优惠券模板库存
     */
    @TableField(value = "stock")
    private Integer stock;

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
     * 每人最多领取张数
     */
    @TableField(value = "max_receive_per_user")
    private Integer maxReceivePerUser;

    /**
     * 优惠券状态：
     * 0：未开始 1：进行中 2：已结束 3：已作废
     */
    @TableField(value = "status")
    private CouponStatusEnum status;

}
