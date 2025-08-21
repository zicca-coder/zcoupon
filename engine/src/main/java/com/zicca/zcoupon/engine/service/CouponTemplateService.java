package com.zicca.zcoupon.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zicca.zcoupon.engine.dao.entity.CouponTemplate;
import com.zicca.zcoupon.engine.dto.req.CouponTemplateQueryReqDTO;
import com.zicca.zcoupon.engine.dto.resp.CouponTemplateQueryRespDTO;

/**
 * 优惠券模板服务
 *
 * @author zicca
 */
public interface CouponTemplateService extends IService<CouponTemplate> {

    /**
     * 查询优惠券模板信息
     *
     * @param requestParam 请求参数
     * @return 优惠券模板信息
     */
    CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam);
}
