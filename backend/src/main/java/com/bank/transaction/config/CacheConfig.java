package com.bank.transaction.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类 - Demo 环境专用
 * 
 * 配置说明：
 * - 使用 Spring Boot 自带的 ConcurrentMapCacheManager
 * - 基于内存存储，重启后数据丢失
 * - 适用于演示和开发环境
 * - 高性能，无网络延迟
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置缓存管理器
     * 使用内存缓存，支持多个缓存区域
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "transactions",           // 交易缓存
            "transactionsByAccount",  // 按账户查询缓存
            "transactionsByStatus",   // 按状态查询缓存
            "transactionStatistics"   // 统计数据缓存
        );
    }
} 