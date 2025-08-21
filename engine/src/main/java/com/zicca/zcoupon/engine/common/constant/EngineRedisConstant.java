package com.zicca.zcoupon.engine.common.constant;

/**
 * Redis常量类
 *
 * @author zicca
 */
public class EngineRedisConstant {

    /**
     * 优惠券模板缓存 Key
     */
    public static final String COUPON_TEMPLATE_KEY = "zcoupon:cache:template:exist:";


    public static final String COUPON_TEMPLATE_LOCK_KEY = "zcoupon:lock:template:";

    public static final String NULL_MARKER = "_null_";

    public static final String EMPTY_HASH_KEY = "_empty_";

}
