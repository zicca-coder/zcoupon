package com.zicca.zcoupon.merchant.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zicca.zcoupon.merchant.admin.common.constants.MerchantAdminRedisConstant;
import com.zicca.zcoupon.merchant.admin.common.enums.CouponStatusEnum;
import com.zicca.zcoupon.merchant.admin.dao.entity.CouponTemplate;
import com.zicca.zcoupon.merchant.admin.dao.mapper.CouponTemplateMapper;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.zicca.zcoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.zicca.zcoupon.merchant.admin.mq.producer.CouponTemplateActiveEventProducer;
import com.zicca.zcoupon.merchant.admin.mq.producer.CouponTemplateExpireEventProducer;
import com.zicca.zcoupon.merchant.admin.service.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import org.apache.curator.shaded.com.google.common.hash.BloomFilter;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zicca.zcoupon.merchant.admin.common.constants.MerchantAdminRedisConstant.COUPON_TEMPLATE_KEY;


/**
 * 优惠券模板服务实现类
 *
 * @author zicca
 */
@Service
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate> implements CouponTemplateService {

    private final CouponTemplateMapper couponTemplateMapper;
    private final StringRedisTemplate stringRedisTemplate;
    // 优惠券模板到期自动生效事件生产者
    private final CouponTemplateActiveEventProducer couponTemplateActiveEventProducer;
    // 优惠券模板过期自动失效事件生产者
    private final CouponTemplateExpireEventProducer couponTemplateExpireEventProducer;
    // 本地布隆过滤器
    private final BloomFilter<String> couponTemplateGuavaBloomFilter;
    // Redis 布隆过滤器
    private final RBloomFilter<String> couponTemplateRedisBloomFilter;
    // lua脚本
    private static final String LUA_SCRIPT = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
            "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";


    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {

        // 新增优惠券模板到数据库
        CouponTemplate template = BeanUtil.toBean(requestParam, CouponTemplate.class);
        template.setStatus(CouponStatusEnum.NOT_START);
        // todo: 设置店铺 ID
        save(template);
        // 优惠券模板信息存储到本地缓存
        // 本地缓存过期时间：较短的时间（有必要在这里加入缓存吗？）

        // 优惠券模板信息存储到分布式缓存
        // 分布式缓存过期时间：优惠券有效期结束时间
        CouponTemplateQueryRespDTO respDTO = BeanUtil.toBean(template, CouponTemplateQueryRespDTO.class);
        Map<String, String> templateCacheHashMap = BeanUtil
                .beanToMap(respDTO, false, true)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                ));
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + template.getId();
        List<String> keys = Collections.singletonList(couponTemplateCacheKey);
        List<String> args = new ArrayList<>(templateCacheHashMap.size() * 2 + 1);
        templateCacheHashMap.forEach((key, value) -> {
            args.add(key);
            args.add(value);
        });
        // 优惠券活动过期时间转换为秒级别的 Unix 时间戳
        args.add(String.valueOf(template.getValidEndTime().getTime() / 1000));
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(LUA_SCRIPT, Long.class),
                keys,
                args
        );
        // 发送延时消息事件，优惠券模板活动开始自动 修改数据库和缓存中优惠券模板状态为进行中
        // 发送延时消息事件，优惠券模板活动到期修改优惠券模板状态  同时本地缓存中淘汰优惠券模板信息
//        couponTemplateExpireEventProducer.sendMessage();
        // 添加优惠券模板 ID 到本地布隆过滤器
        couponTemplateGuavaBloomFilter.put(template.getId().toString());
        // 添加优惠券模板 ID 到分布式布隆过滤器
        couponTemplateRedisBloomFilter.add(template.getId().toString());
    }
}
