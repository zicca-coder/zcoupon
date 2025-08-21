package com.zicca.zcoupon.merchant.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;

import static com.zicca.zcoupon.merchant.admin.common.constants.BloomFilterConstant.COUPON_TEMPLATE_REDIS_BLOOM_FILTER;

/**
 * 布隆过滤器配置
 *
 * @author zicca
 */
public class BloomFilterConfiguration {

    /**
     * 优惠券模板布隆过滤器 | 存储优惠券模板 ID
     */
    @Bean
    public RBloomFilter<Long> couponTemplateBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter(COUPON_TEMPLATE_REDIS_BLOOM_FILTER);
        bloomFilter.tryInit(640L, 0.001);
        return bloomFilter;
    }

}
