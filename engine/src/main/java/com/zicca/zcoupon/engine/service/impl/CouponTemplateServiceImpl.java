package com.zicca.zcoupon.engine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.hash.BloomFilter;
import com.zicca.zcoupon.engine.common.enums.CouponTemplateStatusEnum;
import com.zicca.zcoupon.engine.dao.entity.CouponTemplate;
import com.zicca.zcoupon.engine.dao.mapper.CouponTemplateMapper;
import com.zicca.zcoupon.engine.dto.req.CouponTemplateQueryReqDTO;
import com.zicca.zcoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import com.zicca.zcoupon.engine.service.CouponTemplateService;
import com.zicca.zcoupon.framework.exeception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zicca.zcoupon.engine.common.constant.EngineRedisConstant.COUPON_TEMPLATE_KEY;
import static com.zicca.zcoupon.engine.common.constant.EngineRedisConstant.COUPON_TEMPLATE_LOCK_KEY;

/**
 * 优惠券模板服务实现类
 *
 * @author zicca
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate> implements CouponTemplateService {

    private final Cache<Long, CouponTemplateQueryRespDTO> caffeineCache;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final BloomFilter<Long> couponTemplateGuavaBloomFilter;
    private final RBloomFilter<Long> couponTemplateRedisBloomFilter;
    // lua脚本
    private static final String LUA_SCRIPT = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
            "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";
    // 预编译的Lua脚本对象
    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);


    @Override
    public CouponTemplateQueryRespDTO findCouponTemplate(CouponTemplateQueryReqDTO requestParam) {
        Long couponTemplateId = requestParam.getCouponTemplateId();
        String couponTemplateCacheKey = COUPON_TEMPLATE_KEY + requestParam.getCouponTemplateId();
        Long nullValueMarker = -10086L;
        // 1. 判断本地缓存
        CouponTemplateQueryRespDTO localCache = caffeineCache.getIfPresent(couponTemplateId);
        if (localCache != null) {
            // 判断是否空值标识
            if (nullValueMarker.equals(localCache.getId())) {
                log.info("本地缓存中查询到空值");
                throw new ServiceException("请求优惠券模板不存在...");
            }
            // 不是空值标识，代表有效值，直接返回
            log.info("本地缓存中查询到有效值");
            return localCache;
        }
        // 2. 判断本地布隆过滤器
        if (!couponTemplateGuavaBloomFilter.mightContain(couponTemplateId)) {
            // 证明确实不存在
            log.info("本地布隆过滤器中不存在");
            CouponTemplateQueryRespDTO nullCache = CouponTemplateQueryRespDTO.builder().id(nullValueMarker).build();
            caffeineCache.put(couponTemplateId, nullCache);
            throw new ServiceException("请求优惠券模板不存在...");
        }
        // 3. 查询 Redis
        Map<Object, Object> cacheHashMap = stringRedisTemplate.opsForHash().entries(couponTemplateCacheKey);
        if (MapUtil.isNotEmpty(cacheHashMap)) {
            // 判断是否为空值标识
            if (nullValueMarker.equals(cacheHashMap.get("id"))) {
                // 向本地缓存同步空值标识
                log.info("分布式缓存中查询到空值");
                CouponTemplateQueryRespDTO nullCache = CouponTemplateQueryRespDTO.builder().id(nullValueMarker).build();
                caffeineCache.put(couponTemplateId, nullCache);
                throw new ServiceException("请求优惠券模板不存在...");
            }
            // 不是空值，代表有效值
            log.info("分布式缓存中查询到有效值");
            CouponTemplateQueryRespDTO result = new CouponTemplateQueryRespDTO();
            result = BeanUtil.fillBeanWithMap(cacheHashMap, result, true);
            // 同步到本地缓存
            CouponTemplateQueryRespDTO localResult = new CouponTemplateQueryRespDTO();
            BeanUtil.copyProperties(result, localResult);
            caffeineCache.put(couponTemplateId, localResult);
            return result;
        }
        // 4. 查询 Redis 布隆过滤器
        if (!couponTemplateRedisBloomFilter.contains(couponTemplateId)) {
            // 证明确实不存在，向 Redis 中和 Caffeine 中同步该key为空值标识
            log.info("分布式布隆过滤器中不存在");
            List<String> keys = Collections.singletonList(couponTemplateCacheKey);
            List<String> args = new ArrayList<>(3);
            args.add("id");
            args.add(String.valueOf(nullValueMarker));
            args.add(String.valueOf(System.currentTimeMillis() + 5 * 60 * 1000)); // 5分钟过期
            stringRedisTemplate.execute(redisScript, keys, args.toArray());
            // 同步到本地缓存
            CouponTemplateQueryRespDTO nullCache = CouponTemplateQueryRespDTO.builder().id(nullValueMarker).build();
            caffeineCache.put(couponTemplateId, nullCache);
            throw new ServiceException("请求优惠券模板不存在...");
        }
        // 5. 分布式锁查询数据库
        RLock lock = redissonClient.getLock(COUPON_TEMPLATE_LOCK_KEY + couponTemplateId);
        lock.lock();
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    // 双重判断是否有其他线程完成了缓存重建工作
                    cacheHashMap = stringRedisTemplate.opsForHash().entries(couponTemplateCacheKey);
                    if (MapUtil.isNotEmpty(cacheHashMap)) {
                        // 如果有其他线程重建了空值缓存
                        if (nullValueMarker.equals(cacheHashMap.get("id"))) {
                            // 同步到本地缓存
                            CouponTemplateQueryRespDTO nullCache = CouponTemplateQueryRespDTO.builder().id(nullValueMarker).build();
                            caffeineCache.put(couponTemplateId, nullCache);
                            throw new ServiceException("请求优惠券模板不存在...");
                        }
                        // 不是空值，代表有效值
                        CouponTemplateQueryRespDTO result = new CouponTemplateQueryRespDTO();
                        result = BeanUtil.fillBeanWithMap(cacheHashMap, result, true);
                        // 同步到本地缓存
                        CouponTemplateQueryRespDTO localResult = new CouponTemplateQueryRespDTO();
                        BeanUtil.copyProperties(result, localResult);
                        caffeineCache.put(couponTemplateId, localResult);
                        return result;
                    }

                    // 查询数据库
                    CouponTemplate couponTemplate = lambdaQuery().eq(CouponTemplate::getShopId, requestParam.getShopId())
                            .eq(CouponTemplate::getId, couponTemplateId)
                            .eq(CouponTemplate::getStatus, CouponTemplateStatusEnum.IN_PROGRESS)
                            .one();
                    if (Objects.nonNull(couponTemplate)) {
                        // 放入Redis缓存
                        CouponTemplateQueryRespDTO result = new CouponTemplateQueryRespDTO();
                        BeanUtil.copyProperties(couponTemplate, result);
                        Map<String, String> saveMap = BeanUtil.beanToMap(result).entrySet()
                                .stream().collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue() != null ? entry.getValue().toString() : ""
                                ));
                        List<String> keys = Collections.singletonList(couponTemplateCacheKey);
                        List<String> args = new ArrayList<>(saveMap.size() * 2 + 1);
                        saveMap.forEach((key, value) -> {
                            args.add(key);
                            args.add(value);
                        });
                        args.add(String.valueOf(couponTemplate.getValidEndTime().getTime() / 1000));
                        stringRedisTemplate.execute(redisScript, keys, args.toArray());

                        // 存入本地缓存
                        CouponTemplateQueryRespDTO localResult = new CouponTemplateQueryRespDTO();
                        BeanUtil.copyProperties(result, localResult);
                        caffeineCache.put(couponTemplateId, localResult);
                        return result;
                    } else {
                        // 设置空值标识到 Redis 和本地缓存
                        List<String> keys = Collections.singletonList(couponTemplateCacheKey);
                        List<String> args = new ArrayList<>(3);
                        args.add("id");
                        args.add(String.valueOf(nullValueMarker));
                        args.add(String.valueOf(System.currentTimeMillis() + 5 * 60 * 1000)); // 5分钟过期
                        stringRedisTemplate.execute(redisScript, keys, args.toArray());
                        // 同步到本地缓存
                        CouponTemplateQueryRespDTO nullCache = CouponTemplateQueryRespDTO.builder().id(nullValueMarker).build();
                        caffeineCache.put(couponTemplateId, nullCache);
                        throw new ServiceException("请求优惠券模板不存在...");
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                throw new ServiceException("获取分布式锁失败");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断", e);
            throw new RuntimeException("获取分布式锁被中断");
        } catch (Exception e) {
            log.error("查询优惠券模板异常", e);
            throw new ServiceException("查询优惠券模板异常");
        }

    }
}
