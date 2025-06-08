package com.bank.transaction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "transaction")
public class TransactionConfig {
    // 所有配置值从application.yml读取，不使用硬编码默认值
    private BigDecimal maxAmount; // 单笔交易最大金额
    private BigDecimal minAmount; // 单笔交易最小金额
    private int dailyLimit; // 每日交易笔数限制
    private int retryTimes; // 失败重试次数
    private long timeoutSeconds; // 交易超时时间(秒)

    public BigDecimal getMaxAmount() {return maxAmount;}
    public void setMaxAmount(BigDecimal maxAmount) {this.maxAmount = maxAmount;}
    public BigDecimal getMinAmount() {return minAmount;}
    public void setMinAmount(BigDecimal minAmount) {this.minAmount = minAmount;}
    public int getDailyLimit() {return dailyLimit;}
    public void setDailyLimit(int dailyLimit) {this.dailyLimit = dailyLimit;}
    public int getRetryTimes() {return retryTimes;}
    public void setRetryTimes(int retryTimes) {this.retryTimes = retryTimes;}
    public long getTimeoutSeconds() {return timeoutSeconds;}
    public void setTimeoutSeconds(long timeoutSeconds) {this.timeoutSeconds = timeoutSeconds;}
} 