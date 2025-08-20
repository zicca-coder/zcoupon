package com.zicca.zcoupon.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineTests {

    public static void main(String[] args) {
        // 创建一个缓存实例
        Cache<String, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .maximumSize(100)
                .build();

        // 往缓存中添加数据
        cache.put("key", "value");

        // 从缓存中获取数据
        Object value = cache.getIfPresent("key");
        System.out.println(value);


    }

}
