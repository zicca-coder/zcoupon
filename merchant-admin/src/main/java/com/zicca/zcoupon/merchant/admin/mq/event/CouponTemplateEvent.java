package com.zicca.zcoupon.merchant.admin.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模板事件
 *
 * @author zicca
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTemplateEvent {

    /**
     * 操作类型：ACTIVE(激活)、EXPIRE(过期)
     */
    private String operationType;

    /**
     * 优惠券模板 ID
     */
    private Long couponTemplateId;

    /**
     * 店铺 ID
     */
    private Long shopId;

    /**
     * 操作时间
     */
    private Long operationTime;


}
