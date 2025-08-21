package com.zicca.zcoupon.engine.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.util.Arrays;

/**
 * 用户优惠券状态枚举
 * <p>0：未使用
 * <p>1：锁定
 * <p>2：已使用
 * <p>3：已过期
 * <p>4：已撤回
 *
 * @author zicca
 */
public enum UserCouponStatusEnum implements IEnum<Integer> {

    NOT_USED(0, "未使用"),
    LOCKED(1, "锁定"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期"),
    WITHDRAWN(4, "已撤回");
    @EnumValue
    private Integer code;
    @JsonValue
    private String desc;

    UserCouponStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserCouponStatusEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的用户优惠券状态码"));
    }
}
