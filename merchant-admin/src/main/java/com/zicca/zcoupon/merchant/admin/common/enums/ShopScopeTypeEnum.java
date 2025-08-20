package com.zicca.zcoupon.merchant.admin.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 平台券店铺适用范围枚举类型
 * <p> 0：所有店铺可用
 * <p> 1：部分店铺可用
 *
 * @author zicca
 */
public enum ShopScopeTypeEnum implements IEnum<Integer> {

    ALL(0, "所有店铺可用"),
    PARTIAL(1, "部分店铺可用");
    ;
    @EnumValue
    private Integer code;
    @JsonValue
    private String desc;

    ShopScopeTypeEnum(Integer code, String desc) {
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

    public static ShopScopeTypeEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(value -> value.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的店铺适用范围类型码"));
    }
}
