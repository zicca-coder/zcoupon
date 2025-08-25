package com.zicca.zcoupon.engine.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.zicca.zcoupon.engine.common.constant.BloomFilterConstant.COUPON_TEMPLATE_REDIS_BLOOM_FILTER;

/**
 * 布隆过滤器配置
 *
 * @author zicca
 */
@Configuration
public class BloomFilterConfiguration {

    /**
     * 优惠券模板布隆过滤器 | 存储优惠券模板 ID
     */
    @Bean(value = "couponTemplateRedisBloomFilter")
    public RBloomFilter<Long> couponTemplateRedisBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(COUPON_TEMPLATE_REDIS_BLOOM_FILTER);
        bloomFilter.tryInit(640L, 0.001);
        return bloomFilter;
    }

    @Bean(value = "couponTemplateGuavaBloomFilter")
    public BloomFilter<Long> couponTemplateGuavaBloomFilter() {
        return BloomFilter.create(Funnels.longFunnel(), 640L, 0.001);
    }

}
