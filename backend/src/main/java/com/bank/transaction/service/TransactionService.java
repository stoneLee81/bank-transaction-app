package com.bank.transaction.service;

import java.util.Optional;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.util.PageInfo;
import com.bank.transaction.exception.business.BusinessException;

public interface TransactionService {
    
    /**
     * 创建交易 - 新增一笔交易记录
     */
    Transaction createTransaction(Transaction transaction) throws BusinessException;
    
    /**
     * 修改交易 - 根据ID修改指定交易内容
     */
    Transaction updateTransaction(String id, Transaction transaction) throws BusinessException;
    
    /**
     * 删除交易 - 根据ID删除指定交易
     */
    void deleteTransaction(String id) throws BusinessException;
    
    /**
     * 根据交易ID查询交易记录详情
     */
    Optional<Transaction> getTransactionById(String id) throws BusinessException;
    
    /**
     * 列出所有交易 - 分页展示所有交易记录
     */
    PageInfo<Transaction> getAllTransactions(int page, int size) throws BusinessException;
} 