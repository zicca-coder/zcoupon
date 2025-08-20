package com.zicca.zcoupon.merchant.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模板商品关联表实体（可选，店铺券 & 平台券都能用）
 *
 * @author zicca
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_coupon_template_goods")
public class CouponTemplateGoods extends Base {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 优惠券模板 ID
     */
    @TableField("coupon_template_id")
    private Long couponTemplateId;

    /**
     * 商品 ID
     */
    @TableField("goods_id")
    private String goodsId;

}
