package com.bank.transaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 交易限额配置类
 * 管理所有交易相关的限额和阈值参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "bank.transaction.limits")
public class TransactionLimitConfig {
    
    /**
     * 单笔交易最高限额
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal singleTransactionLimit;
    
    /**
     * 日累计交易限额
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal dailyTransactionLimit;
    
    /**
     * 月累计交易限额
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal monthlyTransactionLimit;
    
    /**
     * 大额交易预警阈值
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal largeAmountThreshold;
    
    /**
     * 最小交易金额
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal minimumTransactionAmount;
    
    /**
     * 账户余额预警阈值
     * 从application.yml读取，不使用硬编码默认值
     */
    private BigDecimal lowBalanceThreshold;
    
    /**
     * 交易超时时间（秒）
     * 从application.yml读取，不使用硬编码默认值
     */
    private int transactionTimeoutSeconds;
    
    /**
     * 最大重试次数
     * 从application.yml读取，不使用硬编码默认值
     */
    private int maxRetryCount;
} 