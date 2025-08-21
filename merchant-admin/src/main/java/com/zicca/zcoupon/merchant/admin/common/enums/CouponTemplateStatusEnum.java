package com.zicca.zcoupon.merchant.admin.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.util.Arrays;

/**
 * 优惠券状态枚举类
 * <p> 0：未开始
 * <p> 1：进行中
 * <p> 2：已结束
 * <p> 3：已作废
 *
 * @author zicca
 */
public enum CouponTemplateStatusEnum implements IEnum<Integer> {
    NOT_START(0, "未开始"),
    IN_PROGRESS(1, "进行中"),
    END(2, "已结束"),
    CANCELED(3, "已作废");
    @EnumValue
    private Integer code;
    @JsonValue
    private String desc;
    CouponTemplateStatusEnum(Integer code, String desc) {
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

    public static CouponTemplateStatusEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的优惠券模板状态码"));
    }
}
