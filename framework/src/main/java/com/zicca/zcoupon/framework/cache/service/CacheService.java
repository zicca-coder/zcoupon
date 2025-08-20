package com.zicca.zcoupon.framework.cache.service;

/**
 * 统一缓存接口
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author zicca
 */
public interface CacheService<K, V> {

    /**
     * 存储数据到缓存
     *
     * @param key   键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 从缓存获取数据
     *
     * @param key 键
     * @return 值
     */
    V get(K key);

    /**
     * 从缓存删除数据
     *
     * @param key 键
     */
    void evict(K key);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 获取缓存名称
     *
     * @return 缓存名称
     */
    String getName();

}
