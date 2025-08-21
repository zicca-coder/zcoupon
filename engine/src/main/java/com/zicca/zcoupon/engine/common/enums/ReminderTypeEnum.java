package com.zicca.zcoupon.engine.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zicca.zcoupon.framework.exeception.ServiceException;

import java.util.Arrays;

/**
 * 预约提醒类型枚举
 *
 * @author zicca
 */
public enum ReminderTypeEnum implements IEnum<Integer> {

    INSITE(0, "站内"),
    EMAIL(1, "邮件"),
    SMS(2, "短信"),
    WECHAT(3, "微信"),
    ;
    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;

    ReminderTypeEnum(Integer code, String desc) {
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

    public static ReminderTypeEnum getByCode(Integer code) {
        return Arrays.stream(values()).filter(item -> item.code.equals(code))
                .findFirst().orElseThrow(() -> new ServiceException("无效的预约提醒类型"));
    }
}
