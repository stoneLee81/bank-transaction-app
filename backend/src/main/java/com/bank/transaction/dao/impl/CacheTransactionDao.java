package com.bank.transaction.dao.impl;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.dao.TransactionDao;

import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存缓存的交易数据访问层实现
 * 
 * 特性：
 * - 使用 ConcurrentHashMap 提供线程安全的内存存储
 * - 支持基础 CRUD 操作
 * - 适用于 Demo 环境，数据在应用重启后会丢失
 * - 高性能，无网络延迟
 */
@Repository
public class CacheTransactionDao implements TransactionDao {
    
    /**
     * 内存缓存存储，使用交易ID作为键
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<String, Transaction> cache = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        String transactionId = transaction.getId();
        cache.put(transactionId, transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Transaction> findByAccountId(String accountId) {
        return cache.values().stream()
                .filter(t -> (t.getFromAccount() != null && accountId.equals(t.getFromAccount().getAccountId())) ||
                            (t.getToAccount() != null && accountId.equals(t.getToAccount().getAccountId())))
                .toList();
    }
    
    /**
     * 根据ID删除交易
     */
    public void deleteById(String id) {
        cache.remove(id);
    }
} 