package com.zicca.zcoupon.engine.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.util.Arrays;

/**
 * 优惠券折扣类型枚举
 * <p> 0：立减券
 * <p> 1：折扣券
 * <p> 2：满减券
 * <p> 3：随机券
 *
 * @author zicca
 */
public enum DiscountTypeEnum implements IEnum<Integer> {

    LITTLE_REDUCTION(0, "立减券"),
    DISCOUNT(1, "折扣券"),
    FULL_REDUCTION(2, "满减券"),
    RANDOM(3, "随机券"),
    ;
    @EnumValue
    private Integer code;
    @JsonValue
    private String desc;

    DiscountTypeEnum(Integer code, String desc) {
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

    public static DiscountTypeEnum getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的优惠券折扣类型码"));
    }
}
