package com.bank.transaction.service;

import com.bank.transaction.model.Account;
import java.util.Optional;
import java.util.List;

/**
 * 账户服务接口
 * 负责账户信息的查询和管理
 */
public interface AccountService {
    
    /**
     * 根据账户ID获取账户信息
     * @param accountId 账户ID
     * @return 账户信息
     */
    Optional<Account> getAccountById(String accountId);
    
    /**
     * 检查账户是否存在
     * @param accountId 账户ID
     * @return 是否存在
     */
    boolean existsById(String accountId);
    
    /**
     * 获取所有账户ID列表
     * @return 账户ID列表
     */
    List<String> getAllAccountIds();
    
    /**
     * 更新账户余额
     * @param accountId 账户ID
     * @param amount 变更金额（正数为增加，负数为减少）
     * @return 更新后的账户信息
     */
    Optional<Account> updateBalance(String accountId, java.math.BigDecimal amount);
} 