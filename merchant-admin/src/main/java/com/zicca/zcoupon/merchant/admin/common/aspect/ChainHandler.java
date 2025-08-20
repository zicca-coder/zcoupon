package com.zicca.zcoupon.merchant.admin.common.aspect;

import java.lang.annotation.*;

/**
 * 责任链参数校验注解
 *
 * @author zicca
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChainHandler {

    String mark();

}
