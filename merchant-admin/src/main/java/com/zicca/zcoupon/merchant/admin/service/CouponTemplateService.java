package com.zicca.zcoupon.merchant.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zicca.zcoupon.merchant.admin.dao.entity.CouponTemplate;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.zicca.zcoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;

public interface CouponTemplateService extends IService<CouponTemplate> {

    /**
     * 创建优惠券模版
     *
     * @param requestParam 请求参数
     */
    void createCouponTemplate(CouponTemplateSaveReqDTO requestParam);

    /**
     * 查询优惠券模板详情
     * Note: 后管接口不存在高并发情况，因此直接查询数据库即可，不需要做缓存
     *
     * @param couponTemplateId 优惠券模板id
     */
    CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId);

    /**
     * 激活优惠券模板
     *
     * @param couponTemplateId 优惠券模板id
     */
    void activeCouponTemplate(Long couponTemplateId);

    /**
     * 终止优惠券模板
     *
     * @param couponTemplateId 优惠券模板id
     */
    void terminateCouponTemplate(Long couponTemplateId);


    /**
     * 取消优惠券模板
     *
     * @param couponTemplateId 优惠券模板id
     */
    void cancelCouponTemplate(Long couponTemplateId);

    /**
     * 增加优惠券模板发行量
     *
     * @param requestParam 请求参数
     */
    void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam);


}
