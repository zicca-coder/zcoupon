package com.zicca.zcoupon.engine.service.handler.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.zicca.zcoupon.engine.dto.resp.CouponTemplateQueryRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/**
 * 优惠券模板缓存服务
 * @author zicca
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CouponTemplateCacheService {

    private final Cache<Long, CouponTemplateQueryRespDTO> caffeineCache;
    private final StringRedisTemplate stringRedisTemplate;

    // lua脚本
    private static final String LUA_SCRIPT = "redis.call('HMSET', KEYS[1], unpack(ARGV, 1, #ARGV - 1)) " +
            "redis.call('EXPIREAT', KEYS[1], ARGV[#ARGV])";
    // 预编译的Lua脚本对象
    private final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
}
