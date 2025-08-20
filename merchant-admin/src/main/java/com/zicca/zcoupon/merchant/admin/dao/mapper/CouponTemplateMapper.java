package com.zicca.zcoupon.merchant.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zicca.zcoupon.merchant.admin.dao.entity.CouponTemplate;
import org.apache.ibatis.annotations.Param;

/**
 * 优惠券模板数据库持久层
 *
 * @author zicca
 */
public interface CouponTemplateMapper extends BaseMapper<CouponTemplate> {

    /**
     * 增加优惠券模板发行量
     * @param couponTemplateId 优惠券模板 id
     * @param shopId 店铺 id
     * @param number 增加的数量
     */
    int increaseNumberCouponTemplate(@Param("couponTemplateId") Long couponTemplateId,
                                     @Param("shopId") Long shopId,
                                     @Param("number") Integer number);
}
