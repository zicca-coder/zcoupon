package com.zicca.zcoupon.engine.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预约提醒取消请求参数
 *
 * @author zicca
 */
@Data
@Schema(description = "预约提醒取消请求参数")
public class ReservationReminderCancelReqDTO {

    @Schema(description = "优惠券模板id", example = "1L")
    private Long couponTemplateId;

    @Schema(description = "门店id", example = "1L")
    private Long shopId;

    @Schema(description = "提醒方式", example = "1")
    private Integer type;

    @Schema(description = "提醒时间", example = "5")
    private Integer remindTime;
}
