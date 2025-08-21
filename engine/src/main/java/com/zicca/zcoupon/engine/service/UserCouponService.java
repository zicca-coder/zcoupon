package com.zicca.zcoupon.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zicca.zcoupon.engine.dao.entity.UserCoupon;

/**
 * 用户优惠券服务
 *
 * @author zicca
 */
public interface UserCouponService extends IService<UserCoupon> {


    void redeemCoupon();




}
