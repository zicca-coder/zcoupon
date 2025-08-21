package com.zicca.zcoupon.merchant.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.zicca.zcoupon.framework.exeception.ServiceException;
import com.zicca.zcoupon.merchant.admin.common.enums.CouponTemplateStatusEnum;
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
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final Cache<Long, CouponTemplateQueryRespDTO> caffeineCache;
    // 优惠券模板事件生产者
    private final CouponTemplateEventProducer couponTemplateEventProducer;
    // 本地布隆过滤器
    private final BloomFilter<Long> couponTemplateGuavaBloomFilter;
    // Redis 布隆过滤器
    private final RBloomFilter<Long> couponTemplateRedisBloomFilter;
    // lua脚本
    private static final String LUA_SCRIPT = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
            "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";
    // 预编译的Lua脚本对象
    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createCouponTemplate(CouponTemplateSaveReqDTO requestParam) {

        // 新增优惠券模板到数据库
        CouponTemplate template = BeanUtil.toBean(requestParam, CouponTemplate.class);
        template.setStatus(CouponTemplateStatusEnum.NOT_START);
        // todo: 设置店铺 ID
        save(template);
        // 优惠券模板信息存储到本地缓存，仅当优惠券模板生效才存储
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
        stringRedisTemplate.execute(redisScript, keys, args);
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
        couponTemplateGuavaBloomFilter.put(template.getId());
        // 添加优惠券模板 ID 到分布式布隆过滤器
        couponTemplateRedisBloomFilter.add(template.getId());
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
        CouponTemplate template = lambdaQuery()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, 1L)
                .one();
        return BeanUtil.toBean(template, CouponTemplateQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void activeCouponTemplate(Long couponTemplateId) {
        // 横向越权校验
        CouponTemplate couponTemplate = lambdaQuery().eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, 1L)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.NOT_START)
                .one();
        if (Objects.isNull(couponTemplate)) {
            throw new ServiceException("优惠券模板异常....");
        }
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponTemplateStatusEnum.IN_PROGRESS) // 未开始的优惠券才可以到期激活
                .update();
        // 修改缓存中优惠券模板状态为进行中
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            // 更新优惠券模板状态
            connection.hashCommands().hSet(couponTemplateCacheKey.getBytes(), "status".getBytes(), CouponTemplateStatusEnum.IN_PROGRESS.toString().getBytes());
            // 获取整个优惠券模板信息
            connection.hashCommands().hGetAll(couponTemplateCacheKey.getBytes());
            return null;
        });
        // 处理返回结果
        Map<byte[], byte[]> rawCacheData = (Map<byte[], byte[]>) results.get(1);
        Map<String, String> cacheHashMap = rawCacheData.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> new String(entry.getKey()),
                        entry -> new String(entry.getValue())
                ));
        CouponTemplateQueryRespDTO result = new CouponTemplateQueryRespDTO();
        result = BeanUtil.fillBeanWithMap(cacheHashMap, result, true);
        // 同步到本地缓存
        CouponTemplateQueryRespDTO localResult = new CouponTemplateQueryRespDTO();
        BeanUtil.copyProperties(result, localResult);
        caffeineCache.put(couponTemplateId, localResult);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void terminateCouponTemplate(Long couponTemplateId) {
        // 横向越权校验
        CouponTemplate couponTemplate = lambdaQuery().eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, 1L)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.IN_PROGRESS) // 进行中的优惠券才可以到期结束
                .one();
        if (Objects.isNull(couponTemplate)) {
            throw new ServiceException("优惠券模板异常....");
        }
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponTemplateStatusEnum.END)
                .update();
        // 删除缓存中的优惠券模板
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        stringRedisTemplate.delete(couponTemplateCacheKey);
        // 删除本地缓存中的优惠券模板
        caffeineCache.invalidate(couponTemplateId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelCouponTemplate(Long couponTemplateId) {
        // 横向越权校验
        CouponTemplate couponTemplate = lambdaQuery().eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, 1L)
                .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.NOT_START) // 未开始的优惠券才可以取消
                .one();
        if (Objects.isNull(couponTemplate)) {
            throw new ServiceException("优惠券模板异常....");
        }
        lambdaUpdate()
                .eq(CouponTemplate::getId, couponTemplateId)
//                .eq(CouponTemplate::getShopId, "")
                .set(CouponTemplate::getStatus, CouponTemplateStatusEnum.CANCELED)
                .update();
        // 删除缓存中的优惠券模板
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + couponTemplateId;
        stringRedisTemplate.delete(couponTemplateCacheKey);
        // 删除本地缓存中的优惠券模板
        caffeineCache.invalidate(couponTemplateId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void increaseNumberCouponTemplate(CouponTemplateNumberReqDTO requestParam) {
        // 横向越权校验
        CouponTemplate couponTemplate = lambdaQuery().eq(CouponTemplate::getId, requestParam.getCouponTemplateId())
//                .eq(CouponTemplate::getShopId, 1L)
                .in(CouponTemplate::getStatus, CouponTemplateStatusEnum.NOT_START) // 未开始的优惠券才可以增加发行量
                .in(CouponTemplate::getStatus, CouponTemplateStatusEnum.IN_PROGRESS) // todo: 进行中的优惠券才可以增加发行量 数据一致性如何保证？
                .one();
        if (Objects.isNull(couponTemplate)) {
            throw new ServiceException("优惠券模板异常....");
        }
        int increased = couponTemplateMapper.increaseNumberCouponTemplate(requestParam.getCouponTemplateId(), 1L, requestParam.getNumber());
        if (!SqlHelper.retBool(increased)) {
            throw new ServiceException("优惠券模板增加发行量失败...");
        }
        // 修改缓存
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + requestParam.getCouponTemplateId();
        stringRedisTemplate.opsForHash().increment(couponTemplateCacheKey, "stock", requestParam.getNumber());
        // 增加本地缓存中的优惠券模板库存
        CouponTemplateQueryRespDTO localCache = caffeineCache.getIfPresent(requestParam.getCouponTemplateId());
        if (localCache != null) {
            localCache.setStock(localCache.getStock() + requestParam.getNumber());
        }
    }
}
