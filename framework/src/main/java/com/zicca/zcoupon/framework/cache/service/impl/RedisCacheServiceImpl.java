package com.zicca.zcoupon.framework.cache.service.impl;

import com.zicca.zcoupon.framework.cache.service.CacheService;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 分布式缓存
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author zicca
 */
public class RedisCacheServiceImpl<K, V> implements CacheService<K, V> {

    private final RedisTemplate<K, V> redisTemplate;
    private final String cacheName;
    private final long expireTime;

    public RedisCacheServiceImpl(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.cacheName = "redisCache";
        this.expireTime = 30;
    }


    @Override
    public void put(K key, V value) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void evict(K key) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getName() {
        return "";
    }
}
