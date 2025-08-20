package com.zicca.zcoupon.merchant.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 平台券适用店铺关联表实体类
 * 用于处理平台券适用店铺的情况
 * 查询某个店铺可用券时，需要查询此表
 *
 * @author zicca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_coupon_template_shop")
public class CouponTemplatePlatformShop extends Base {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券模板ID
     */
    @TableField("template_id")
    private Long templateId;

    /**
     * 店铺编号
     */
    @TableField("shop_id")
    private Long shopId;

}