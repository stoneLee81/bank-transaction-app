package com.bank.transaction.service;

import com.bank.transaction.model.Transaction;
import java.util.List;

/**
 * 交易工具服务接口
 * 提供交易生成和银行后续处理的统一服务
 */
public interface TransactionToolService {
    
    /**
     * 生成银行交易ID（银行流水号格式）
     */
    String generateBankTransactionId();
    
    /**
     * 生成交易参考号
     */
    String generateReferenceNumber();
    
    /**
     * 生成幂等性键
     */
    String generateIdempotencyKey();
    
    /**
     * 银行后续处理 - 统一处理创建/更新/删除后的后续操作
     */
    void performPostTransactionProcessing(Transaction transaction, String operationType);
    
    /**
     * 根据时间排序获取交易ID列表（分页）
     * 统一处理缓存优化查询逻辑
     * 
     * @param page 页码（从0开始）
     * @param size 页大小
     * @return 按时间倒序排列的交易ID列表
     */
    List<String> getTransactionIdsByTime(int page, int size);
    
    /**
     * 获取交易总数
     * 
     * @return 交易总数
     */
    int getTotalTransactionCount();
    
    /**
     * 维护交易时间索引 - 在交易创建时调用
     * 
     * @param transaction 新创建的交易
     */
    void maintainTimeIndex(Transaction transaction);
    
    /**
     * 移除交易时间索引 - 在交易删除时调用
     * 
     * @param transactionId 要删除的交易ID
     */
    void removeFromTimeIndex(String transactionId);
} 