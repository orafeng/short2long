package com.base.short2long.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChannelMap<K, V> {
    private final Cache<K, V> cache;

    public ChannelMap(long duration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, timeUnit) // 指定缓存过期时间
                .build();
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    /**
     * 注意: 若value为空，build方法会抛出java.lang.NullPointerException异常
     */
    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public void remove(K key) {
        cache.invalidate(key);
    }

    public void clear() {
        cache.invalidateAll();
    }

    public Cache<K, V> getCache() {
        return cache;
    }

    public static void main(String[] args) {
        final ChannelMap channelMap = new ChannelMap(5, TimeUnit.SECONDS);

        // 存放键值对
        channelMap.put("One", 1);
        channelMap.put("Two", 2);
        channelMap.put("Three", 3);
        channelMap.put("Four", 4);

        channelMap.remove("One");
        channelMap.remove("Two");

        // 获取键值对
        System.out.println("One:" + channelMap.get("One"));
        System.out.println("Two:" + channelMap.get("Two"));

        // 等待缓存到期，此时超时缓存中的键值已被自动清除
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 再次获取键值对，此时使用get方法返回的值为null
        System.out.println("One:" + channelMap.get("One"));
        System.out.println("Two:" + channelMap.get("Two"));

    }
}