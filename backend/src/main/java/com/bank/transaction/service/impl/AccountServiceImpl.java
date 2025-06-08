package com.bank.transaction.service.impl;

import com.bank.transaction.service.AccountService;
import com.bank.transaction.model.Account;
import com.bank.transaction.model.Bank;
import com.bank.transaction.util.Constants.AccountStatus;
import com.bank.transaction.util.Constants.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 账户服务实现类
 * 使用内存缓存模拟账户数据存储
 */
@Service
public class AccountServiceImpl implements AccountService {
    
    // 模拟账户缓存数据
    private final Map<String, Account> accountCache = new ConcurrentHashMap<>();
    
    public AccountServiceImpl() {
        // 初始化一些测试账户数据
        initializeTestAccounts();
    }
    
    @Override
    public Optional<Account> getAccountById(String accountId) {
        return Optional.ofNullable(accountCache.get(accountId));
    }
    
    @Override
    public boolean existsById(String accountId) {
        return accountCache.containsKey(accountId);
    }
    
    @Override
    public List<String> getAllAccountIds() {
        return List.copyOf(accountCache.keySet());
    }
    
    @Override
    public Optional<Account> updateBalance(String accountId, BigDecimal amount) {
        Account account = accountCache.get(accountId);
        if (account == null) {
            return Optional.empty();
        }
        
        // 计算新余额
        BigDecimal newBalance = account.getBalance().add(amount);
        
        // 检查余额不能为负数
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("账户余额不足，无法完成交易");
        }
        
        // 更新余额
        account.setBalance(newBalance);
        accountCache.put(accountId, account);
        
        return Optional.of(account);
    }
    
    /**
     * 初始化测试账户数据
     */
    private void initializeTestAccounts() {
        // 创建测试银行
        Bank icbc = new Bank("BANK001", "中国工商银行");
        Bank ccb = new Bank("BANK002", "中国建设银行");
        Bank abc = new Bank("BANK003", "中国农业银行");
        
        // 创建测试账户
        createTestAccount("ACC001", "6222021234567890", "张三", icbc, new BigDecimal("10000"), Currency.CNY, AccountStatus.ACTIVE);
        createTestAccount("ACC002", "6222021234567891", "李四", ccb, new BigDecimal("5000"), Currency.CNY, AccountStatus.ACTIVE);
        createTestAccount("ACC003", "6222021234567892", "王五", icbc, new BigDecimal("8000"), Currency.CNY, AccountStatus.ACTIVE);
        createTestAccount("ACC004", "6222021234567893", "赵六", abc, new BigDecimal("12000"), Currency.CNY, AccountStatus.ACTIVE);
        createTestAccount("ACC005", "6222021234567894", "孙七", ccb, new BigDecimal("3000"), Currency.CNY, AccountStatus.FROZEN);
    }
    
    /**
     * 创建测试账户
     */
    private void createTestAccount(String accountId, String accountNumber, String accountName, 
                                 Bank bank, BigDecimal balance, Currency currency, AccountStatus status) {
        Account account = new Account(accountId, accountNumber, accountName, bank, balance, currency, status);
        accountCache.put(accountId, account);
    }
} 