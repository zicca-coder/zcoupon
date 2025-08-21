package com.zicca.zcoupon.engine.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 优惠券模板查询接口请求参数实体
 *
 * @author zicca
 */
@Data
@Schema(description = "优惠券模板查询接口请求参数实体")
public class CouponTemplateQueryReqDTO {

    /**
     * 店铺编号
     */
    @Schema(description = "店铺编号", example = "1810714735922956666", required = true)
    private String shopId;

    /**
     * 优惠券模板id
     */
    @Schema(description = "优惠券模板id", example = "1925153463886295042", required = true)
    private Long couponTemplateId;

}
