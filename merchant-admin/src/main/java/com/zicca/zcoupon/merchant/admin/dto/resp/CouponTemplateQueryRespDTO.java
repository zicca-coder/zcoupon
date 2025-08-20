package com.zicca.zcoupon.merchant.admin.dto.resp;

import com.zicca.zcoupon.merchant.admin.common.enums.CouponStatusEnum;
import com.zicca.zcoupon.merchant.admin.common.enums.DiscountTargetEnum;
import com.zicca.zcoupon.merchant.admin.common.enums.DiscountTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 优惠券模板查询响应参数
 *
 * @author zicca
 */
@Data
@Schema(description = "优惠券模板查询响应参数")
public class CouponTemplateQueryRespDTO {

    @Schema(description = "优惠券模板ID", example = "1L")
    private Long id;

    @Schema(description = "优惠券名称", example = "天天神券")
    private String name;

    @Schema(description = "优惠券所属店铺", example = "1L")
    private Long shopId;

    @Schema(description = "优惠对象：0-指定商品可用 1-全店通用", example = "1")
    private DiscountTargetEnum target;

    @Schema(description = "优惠券可用商品，仅当优惠对象为指定商品可用时才需填写", example = "[1L,2L]")
    private List<Long> goodIds;

    @Schema(description = "优惠券类型：0-立减券 1-折扣券 2-满减券 3-随机券", example = "1")
    private DiscountTypeEnum type;

    @Schema(description = "优惠券面值（立减金额/折扣率/随机最小值等）", example = "10")
    private BigDecimal faceValue;

    @Schema(description = "优惠券满减门槛金额（仅当优惠券为满减券时才需填写）", example = "20")
    private BigDecimal minAmount;

    @Schema(description = "优惠券折扣最大金额（仅当优惠券为折扣券时才需填写）", example = "30")
    private BigDecimal maxDiscountAmount;

    @Schema(description = "优惠券随机券最大金额（仅当优惠券为随机券时才需填写）", example = "40")
    private BigDecimal maxRandomAmount;

    @Schema(description = "优惠券库存", example = "100")
    private Integer stock;

    @Schema(description = "优惠券有效期开始时间", example = "2023-01-01 00:00:00")
    private Date validStartTime;

    @Schema(description = "优惠券有效期结束时间", example = "2023-01-01 00:00:00")
    private Date validEndTime;

    @Schema(description = "优惠券用户领取限制（-1为不限制）", example = "1")
    private Integer maxReceivePerUser;

    @Schema(description = "优惠券状态：0-未开始 1-进行中 2-已结束 3-已作废", example = "0")
    private CouponStatusEnum status;
}
