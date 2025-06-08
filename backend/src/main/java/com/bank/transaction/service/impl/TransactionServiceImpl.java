package com.bank.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import com.bank.transaction.util.VirtualThreadExecutor;
import com.bank.transaction.service.TransactionService;
import com.bank.transaction.service.TransactionToolService;
import com.bank.transaction.config.TransactionLimitConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import com.bank.transaction.exception.business.BusinessException;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.model.Account;
import com.bank.transaction.dao.TransactionDao;
import com.bank.transaction.service.AccountService;
import com.bank.transaction.util.Constants.ErrorCode;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.AccountStatus;
import com.bank.transaction.util.PageInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionDao transactionDao;
    
    @Autowired
    private TransactionToolService transactionToolService;
    
    @Autowired
    private TransactionLimitConfig limitConfig;
    
    @Autowired
    private AccountService accountService;
    
    @Override
    @CachePut(value = "transactions", key = "#result.id", condition = "#result != null")
    public Transaction createTransaction(Transaction transaction) {
        try {
            // 1. 银行业务校验 - 幂等性检查
            if (transaction.getIdempotencyKey() != null 
                && existsByIdempotencyKey(transaction.getIdempotencyKey())) {
                throw new BusinessException(ErrorCode.TRANSACTION_CONFLICT, "重复的交易请求，幂等性键已存在");
            }
            
            // 2. 根据账户ID获取完整账户信息
            populateAccountsFromIds(transaction);
            
            // 3. 银行业务校验 - 账户状态检查
            validateAccountStatus(transaction);
            
            // 4. 银行业务校验 - 余额充足性检查（仅对支出类交易）
            validateAccountBalance(transaction);
            
            // 4. 银行业务校验 - 交易限额检查
            validateTransactionLimits(transaction);
            
            // 5. 生成银行标准交易数据
            prepareTransactionData(transaction);
            
            // 6. 根据交易类型设置账户信息
            configureAccountsByTransactionType(transaction);
            
            // 7. 保存交易记录
            Transaction savedTransaction = transactionDao.save(transaction);
            
            // 8. 维护时间索引
            transactionToolService.maintainTimeIndex(savedTransaction);
            
            // 9. 银行后续处理（实际银行系统中可能需要发送到清算系统、记录审计日志等）可交给消息，统一处理
            transactionToolService.performPostTransactionProcessing(savedTransaction, "CREATE");
            
            return savedTransaction;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建交易失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public void deleteTransaction(String id) {
        // 银行业务规则：不允许删除交易记录
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "银行交易记录不允许删除，如需处理请联系管理员");
    }

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public Optional<Transaction> getTransactionById(String id) {
        return transactionDao.findById(id);
    }

    @Override
    public PageInfo<Transaction> getAllTransactions(int page, int size) {
        try {
            // 1. 根据时间排序，获取到 page, size 的 transIds
            List<String> transactionIds = transactionToolService.getTransactionIdsByTime(page, size);
            
            if (transactionIds.isEmpty()) {
                int totalCount = transactionToolService.getTotalTransactionCount();
                return PageInfo.of(List.of(), page, size, totalCount);
            }
            
            // 2. 开虚拟线程并行查询 transactionDao.findById(transId)
            List<Transaction> transactions = VirtualThreadExecutor.runWithVirtualThreadsLogic(
                transactionIds,
                transactionDao::findById
            ).stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
            
            // 3. 返回 PageInfo<Transaction>
            int totalCount = transactionToolService.getTotalTransactionCount();
            return PageInfo.of(transactions, page, size, totalCount);
            
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询交易列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查幂等性键是否已存在（模拟实现）
     */
    private boolean existsByIdempotencyKey(String idempotencyKey) {
        // TODO: 实际实现应查询数据库
        return false;
    }
    
    /**
     * 根据账户ID从缓存获取完整账户信息
     */
    private void populateAccountsFromIds(Transaction transaction) {
        // 获取转出账户信息
        if (transaction.getFromAccountId() != null && !transaction.getFromAccountId().trim().isEmpty()) {
            Optional<Account> fromAccount = accountService.getAccountById(transaction.getFromAccountId());
            if (!fromAccount.isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_ACCOUNT, "转出账户不存在: " + transaction.getFromAccountId());
            }
            transaction.setFromAccount(fromAccount.get());
        }
        
        // 获取转入账户信息
        if (transaction.getToAccountId() != null && !transaction.getToAccountId().trim().isEmpty()) {
            Optional<Account> toAccount = accountService.getAccountById(transaction.getToAccountId());
            if (!toAccount.isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_ACCOUNT, "转入账户不存在: " + transaction.getToAccountId());
            }
            transaction.setToAccount(toAccount.get());
        }
    }
    
    /**
     * 银行业务校验 - 账户状态检查
     */
    private void validateAccountStatus(Transaction transaction) {
        // 检查转出账户状态
        if (transaction.getFromAccount() != null 
            && transaction.getFromAccount().getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT, 
                "转出账户状态异常: " + transaction.getFromAccount().getStatus().getDescription());
        }
        
        // 检查转入账户状态
        if (transaction.getToAccount() != null 
            && transaction.getToAccount().getStatus() == AccountStatus.CLOSED) {
            throw new BusinessException(ErrorCode.INVALID_ACCOUNT, 
                "转入账户已关闭，无法接收资金");
        }
    }
    
    /**
     * 银行业务校验 - 余额充足性检查
     */
    private void validateAccountBalance(Transaction transaction) {
        // 只对需要扣款的交易类型进行余额检查
        if (!isDebitTransaction(transaction.getType()) || transaction.getFromAccount() == null) {
            return;
        }
        
        if (transaction.getFromAccount().getBalance().compareTo(transaction.getAmount()) <= 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE, 
                "账户余额不足，当前余额: " + transaction.getFromAccount().getBalance() + 
                "，交易金额: " + transaction.getAmount());
        }
    }
    
    /**
     * 银行业务校验 - 交易限额检查
     * 使用动态配置进行金额范围和限额校验
     */
    private void validateTransactionLimits(Transaction transaction) {
        // 1. 基本金额范围校验（动态配置）
        if (limitConfig.getMinimumTransactionAmount() != null 
            && transaction.getAmount().compareTo(limitConfig.getMinimumTransactionAmount()) < 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, 
                "交易金额不能小于最小限额: " + limitConfig.getMinimumTransactionAmount());
        }
        
        if (limitConfig.getSingleTransactionLimit() != null 
            && transaction.getAmount().compareTo(limitConfig.getSingleTransactionLimit()) > 0) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, 
                "单笔交易金额不能超过限额: " + limitConfig.getSingleTransactionLimit());
        }
        
        // 2. 大额交易预警
        if (limitConfig.getLargeAmountThreshold() != null 
            && transaction.getAmount().compareTo(limitConfig.getLargeAmountThreshold()) >= 0) {
            // TODO: 记录大额交易日志，发送预警通知
            System.out.println("大额交易预警: 交易金额 " + transaction.getAmount() + " 超过预警阈值 " + limitConfig.getLargeAmountThreshold());
        }
        
        // TODO: 日累计限额检查、月累计限额检查等高级业务规则
    }
    
    /**
     * 生成银行标准交易数据
     */
    private void prepareTransactionData(Transaction transaction) {
        // 生成唯一交易ID（银行流水号格式）
        transaction.setId(transactionToolService.generateBankTransactionId());
        
        // 设置交易时间戳
        transaction.setTimestamp(LocalDateTime.now());
        
        // 设置初始状态为待处理
        transaction.setStatus(TransactionStatus.PENDING);
        
        // 生成交易参考号（如果未提供）
        if (transaction.getReferenceNumber() == null || transaction.getReferenceNumber().trim().isEmpty()) {
            transaction.setReferenceNumber(transactionToolService.generateReferenceNumber());
        }
        
        // 如果未提供幂等性键，则生成一个
        if (transaction.getIdempotencyKey() == null || transaction.getIdempotencyKey().trim().isEmpty()) {
            transaction.setIdempotencyKey(transactionToolService.generateIdempotencyKey());
        }
    }
    
    /**
     * 根据交易类型配置账户信息
     */
    private void configureAccountsByTransactionType(Transaction transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
                // 存款：只需要转入账户
                transaction.setFromAccount(null); // 存款无转出账户
                transaction.setDirection("IN");
                break;
                
            case WITHDRAWAL:
                // 取款：只需要转出账户
                transaction.setToAccount(null); // 取款无转入账户
                transaction.setDirection("OUT");
                break;
                
            case TRANSFER:
                // 转账：需要转出和转入账户
                if (transaction.getFromAccount() == null || transaction.getToAccount() == null) {
                    throw new BusinessException(ErrorCode.VALIDATION_ERROR, "转账交易必须包含转出账户和转入账户");
                }
                transaction.setDirection("OUT"); // 从转出账户角度看是支出
                break;
                
            case PAYMENT:
                // 支付：通常是支出类交易
                transaction.setDirection("OUT");
                break;
                
            case REFUND:
                // 退款：通常是收入类交易
                transaction.setDirection("IN");
                break;
                
            default:
                throw new BusinessException(ErrorCode.VALIDATION_ERROR, "不支持的交易类型: " + transaction.getType());
        }
    }
    
    /**
     * 判断是否为扣款类交易
     */
    private boolean isDebitTransaction(TransactionType type) {
        return type == TransactionType.WITHDRAWAL || 
               type == TransactionType.TRANSFER || 
               type == TransactionType.PAYMENT;
    }
    

    
    @Override
    @CachePut(value = "transactions", key = "#id")
    public Transaction updateTransaction(String id, Transaction transaction) {
        try {
            // 1. 获取现有交易记录
            Optional<Transaction> existingOpt = transactionDao.findById(id);
            if (!existingOpt.isPresent()) {
                throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND, "交易不存在: " + id);
            }
            Transaction existingTransaction = existingOpt.get();
            
            // 2. 银行业务校验 - 交易状态检查（只允许PENDING或FAILED状态修改）
            validateTransactionStatusForUpdate(existingTransaction);

            // 3. 更新remark字段
            Transaction updatedTransaction = updateRemarkOnly(existingTransaction, transaction);

            // 5. 保存更新
            Transaction result = transactionDao.save(updatedTransaction);
            
            // 6. 银行后续处理
            transactionToolService.performPostTransactionProcessing(result, "UPDATE");
            
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新交易失败: " + e.getMessage());
        }
    }
    
    /**
     * 银行业务校验 - 更新操作的交易状态检查
     * 只允许PENDING或FAILED状态的交易修改remark
     */
    private void validateTransactionStatusForUpdate(Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "已完成的交易不允许修改备注");
        }
        if (transaction.getStatus() != TransactionStatus.PENDING && transaction.getStatus() != TransactionStatus.FAILED) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "只有待处理或失败状态的交易可以修改备注");
        }
    }



    /**
     * 只更新remark字段
     */
    private Transaction updateRemarkOnly(Transaction existing, Transaction update) {
        Transaction updatedTransaction = new Transaction();
        BeanUtils.copyProperties(existing, updatedTransaction);
        
        // 只允许更新remark字段
        if (update.getRemark() != null) {
            updatedTransaction.setRemark(update.getRemark());
        }
        
        return updatedTransaction;
    }
} 