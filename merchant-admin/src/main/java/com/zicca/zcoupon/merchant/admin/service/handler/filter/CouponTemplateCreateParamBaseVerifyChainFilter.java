package com.zicca.zcoupon.merchant.admin.service.handler.filter;

import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.zicca.zcoupon.merchant.admin.service.basics.chain.MerchantAdminAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.zicca.zcoupon.merchant.admin.common.enums.ChainBizMarkEnum.MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY;

/**
 * 验证优惠券创建接口参数是否正确责任链组件 | 验证参数基本关系是否正确
 *
 * @author zicca
 */
@Component
public class CouponTemplateCreateParamBaseVerifyChainFilter implements MerchantAdminAbstractChainHandler<CouponTemplateSaveReqDTO> {
    @Override
    public void handle(CouponTemplateSaveReqDTO requestParam) {

    }

    @Override
    public String mark() {
        return MERCHANT_ADMIN_CREATE_COUPON_TEMPLATE_KEY.name();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
