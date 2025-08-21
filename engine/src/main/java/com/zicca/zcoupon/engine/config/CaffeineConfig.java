package com.zicca.zcoupon.engine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.zicca.zcoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author zicca
 */
@Configuration
public class CaffeineConfig {

    /**
     * 优惠券模板本地缓存
     *
     * @return 缓存对象
     */
    @Bean
    public Cache<Long, CouponTemplateQueryRespDTO> couponTemplateCache() {
        Long nullValueMarker = -10086L;
        return Caffeine.newBuilder()
                .maximumSize(1000) // 最大缓存1000个条目
                .expireAfter(new Expiry<Long, CouponTemplateQueryRespDTO>() {
                    @Override
                    public long expireAfterCreate(Long key, CouponTemplateQueryRespDTO value, long currentTime) {
                        // 为空值设置较短的过期时间
                        if (value != null && nullValueMarker.equals(value.getId())) {
                            return TimeUnit.MINUTES.toNanos(5); // 空值设置5分钟过期
                        }
                        return TimeUnit.MINUTES.toNanos(30); // 有效值设置30分钟过期
                    }

                    @Override
                    public long expireAfterUpdate(Long key, CouponTemplateQueryRespDTO value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(Long key, CouponTemplateQueryRespDTO value, long currentTime, @NonNegative long currentDuration) {
                        return currentDuration;
                    }
                })
                .build();
    }


}
