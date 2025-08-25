package com.zicca.zcoupon.engine.controller;

import com.zicca.zcoupon.engine.dto.req.CouponTemplateQueryReqDTO;
import com.zicca.zcoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.zicca.zcoupon.engine.service.CouponTemplateService;
import com.zicca.zcoupon.framework.result.Result;
import com.zicca.zcoupon.framework.web.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券模板 Controller
 *
 * @author zicca
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/engine/template")
@Tag(name = "优惠券模板管理", description = "优惠券模板接口管理")
public class CouponTemplateController {

    private final CouponTemplateService couponTemplateService;

    @PostMapping("/query")
    @Operation(summary = "查询优惠券模板", description = "查询优惠券模板")
    @ApiResponse(
            responseCode = "200", description = "查询优惠券模板成功",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CouponTemplateQueryRespDTO.class))
    )
    public Result<CouponTemplateQueryRespDTO> query(@RequestBody CouponTemplateQueryReqDTO requestParam) {
        return Results.success(couponTemplateService.findCouponTemplate(requestParam));
    }

}
