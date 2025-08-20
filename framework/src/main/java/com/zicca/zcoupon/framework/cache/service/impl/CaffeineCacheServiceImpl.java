package com.zicca.zcoupon.framework.cache.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zicca.zcoupon.framework.cache.service.CacheService;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

/**
 * caffeine缓存实现类
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author zicca
 */
public class CaffeineCacheServiceImpl<K, V> implements CacheService<K, V> {

    private final Cache<K, V> cache;
    private final String cacheName;

    public CaffeineCacheServiceImpl() {
        this.cacheName = "caffeineCache";
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void evict(K key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public String getName() {
        return cacheName;
    }
}
