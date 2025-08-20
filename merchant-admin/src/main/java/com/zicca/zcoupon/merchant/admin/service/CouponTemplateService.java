package com.zicca.zcoupon.merchant.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zicca.zcoupon.merchant.admin.dao.entity.CouponTemplate;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;

public interface CouponTemplateService extends IService<CouponTemplate> {

    /**
     * 创建优惠券模版
     * @param requestParam 请求参数
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);

}
