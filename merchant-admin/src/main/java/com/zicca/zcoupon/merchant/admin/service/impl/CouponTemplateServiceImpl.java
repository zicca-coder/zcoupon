package com.zicca.zcoupon.merchant.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zicca.zcoupon.framework.exeception.ServiceException;
import com.zicca.zcoupon.merchant.admin.common.enums.CouponStatusEnum;
import com.zicca.zcoupon.merchant.admin.dao.entity.CouponTemplate;
import com.zicca.zcoupon.merchant.admin.dao.mapper.CouponTemplateMapper;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateNumberReqDTO;
import com.zicca.zcoupon.merchant.admin.dto.req.CouponTemplateSaveReqDTO;
import com.zicca.zcoupon.merchant.admin.dto.resp.CouponTemplateQueryRespDTO;
import com.zicca.zcoupon.merchant.admin.mq.event.CouponTemplateEvent;
import com.zicca.zcoupon.merchant.admin.mq.producer.CouponTemplateEventProducer;
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
    // 优惠券模板事件生产者
    private final CouponTemplateEventProducer couponTemplateEventProducer;
    // 本地布隆过滤器
    private final BloomFilter<String> couponTemplateGuavaBloomFilter;
    // Redis 布隆过滤器
    private final RBloomFilter<String> couponTemplateRedisBloomFilter;
    // lua脚本
    private static final String LUA_SCRIPT = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
            "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";
    // 预编译的Lua脚本对象
    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);


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
                redisScript,
                keys,
                args
        );
        // 发送延时消息事件，优惠券模板活动开始自动 修改数据库和缓存中优惠券模板状态为进行中
        CouponTemplateEvent templateActiveEvent = CouponTemplateEvent.builder()
                .operationType("ACTIVE")
                .couponTemplateId(template.getId())
                .shopId(template.getShopId())
                .build();
        couponTemplateEventProducer.sendMessage(templateActiveEvent);

        // 发送延时消息事件，优惠券模板活动到期修改优惠券模板状态  同时本地缓存中淘汰优惠券模板信息
        CouponTemplateEvent templateExpireEvent = CouponTemplateEvent.builder()
                .operationType("EXPIRE")
                .couponTemplateId(template.getId())
                .shopId(template.getShopId())
                .operationTime(template.getValidEndTime().getTime())
                .build();
        couponTemplateEventProducer.sendMessage(templateExpireEvent);
        // 添加优惠券模板 ID 到本地布隆过滤器
//        couponTemplateGuavaBloomFilter.put(template.getId().toString());
//        // 添加优惠券模板 ID 到分布式布隆过滤器
//        couponTemplateRedisBloomFilter.add(template.getId().toString());
    }

    @Override
    public CouponTemplateQueryRespDTO findCouponTemplateById(String couponTemplateId) {
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        Map<Object, Object> cacheHashMap = stringRedisTemplate.opsForHash().entries(couponTemplateCacheKey);
        if (cacheHashMap != null && !cacheHashMap.isEmpty()) {
            CouponTemplateQueryRespDTO couponTemplateQueryRespDTO = new CouponTemplateQueryRespDTO();
            BeanUtil.fillBeanWithMap(cacheHashMap, couponTemplateQueryRespDTO, true);
            return couponTemplateQueryRespDTO;
        }
        // todo: 加上店铺ID查询条件
        CouponTemplate template = lambdaQuery().eq(CouponTemplate::getId, couponTemplateId).one();
        return BeanUtil.toBean(template, CouponTemplateQueryRespDTO.class);
    }

    @Override
    public void activeCouponTemplate(Long couponTemplateId) {
        // todo: 横向越权校验
        // 检查优惠券模板是否存在
        // 检查优惠券模板状态
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponStatusEnum.IN_PROGRESS)
                .update();
        // 修改缓存中优惠券模板状态为作废状态
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        stringRedisTemplate.opsForHash().put(couponTemplateCacheKey, "status", CouponStatusEnum.IN_PROGRESS.toString());
    }

    @Override
    public void terminateCouponTemplate(Long couponTemplateId) {
        // todo: 横向越权校验
        // 检查优惠券模板是否存在
        // 检查优惠券模板状态
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponStatusEnum.END)
                .update();
        // 修改缓存中优惠券模板状态为作废状态
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        stringRedisTemplate.opsForHash().put(couponTemplateCacheKey, "status", CouponStatusEnum.END.toString());
    }

    @Override
    public void cancelCouponTemplate(Long couponTemplateId) {
        // todo: 横向越权校验
        // 检查优惠券模板是否存在
        // 检查优惠券模板状态
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponStatusEnum.CANCELED)
                .update();
        // 修改缓存中优惠券模板状态为作废状态
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        stringRedisTemplate.opsForHash().put(couponTemplateCacheKey, "status", CouponStatusEnum.CANCELED.toString());
    }

    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {
        // todo: 横向越权校验
        // 检查优惠券模板是否存在
        // 检查优惠券模板状态
        int increased = couponTemplateMapper.increaseNumberCouponTemplate(requestParam.getCouponTemplateId(), 1L, requestParam.getNumber());
        if (!SqlHelper.retBool(increased)) {
            throw new ServiceException("优惠券模板增加发行量失败...");
        }
        // 修改缓存
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + requestParam.getCouponTemplateId();
        stringRedisTemplate.opsForHash().increment(couponTemplateCacheKey, "stock", requestParam.getNumber());

    }
}
