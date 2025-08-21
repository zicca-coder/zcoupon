package com.zicca.zcoupon.engine.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.util.Arrays;

/**
 * 优惠对象类型枚举类
 * <p> 0：指定商品可用
 * <p> 1：所有商品可用
 *
 * @author zicca
 */
public enum DiscountTargetEnum implements IEnum<Integer> {

    ALL(1, "所有商品可用"),
    PARTIAL(0, "指定商品可用");
    ;
    @EnumValue
    private Integer code;
    @JsonValue
    private String desc;

    DiscountTargetEnum(Integer code, String desc) {
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

    public static DiscountTargetEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的优惠对象类型码"));
    }
}
