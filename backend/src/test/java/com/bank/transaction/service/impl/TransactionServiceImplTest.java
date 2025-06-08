package com.bank.transaction.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.transaction.dao.TransactionDao;
import com.bank.transaction.exception.business.BusinessException;
import com.bank.transaction.model.Account;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionToolService;
import com.bank.transaction.service.AccountService;
import com.bank.transaction.config.TransactionLimitConfig;
import com.bank.transaction.util.Constants.ErrorCode;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.AccountStatus;
import com.bank.transaction.util.Constants.Currency;
import com.bank.transaction.util.PageInfo;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@DisplayName("交易服务实现类单元测试")
class TransactionServiceImplTest {

    @Mock
    private TransactionDao transactionDao;
    
    @Mock
    private TransactionToolService transactionToolService;
    
    @Mock
    private AccountService accountService;
    
    @InjectMocks
    private TransactionServiceImpl transactionService;
    
    private TransactionLimitConfig limitConfig;
    
    private Transaction testTransaction;
    private Account fromAccount;
    private Account toAccount;
    
    @BeforeEach
    void setUp() {
        // 创建真实的配置对象
        limitConfig = new TransactionLimitConfig();
        limitConfig.setSingleTransactionLimit(new BigDecimal("50000"));
        limitConfig.setMinimumTransactionAmount(new BigDecimal("0.01"));
        limitConfig.setLargeAmountThreshold(new BigDecimal("10000"));
        
        // 注入到服务中
        ReflectionTestUtils.setField(transactionService, "limitConfig", limitConfig);
        
        // 准备测试数据
        fromAccount = new Account();
        fromAccount.setAccountNumber("ACC001");
        fromAccount.setBalance(new BigDecimal("10000"));
        fromAccount.setStatus(AccountStatus.ACTIVE);
        fromAccount.setCurrency(Currency.CNY);
        
        toAccount = new Account();
        toAccount.setAccountNumber("ACC002");
        toAccount.setBalance(new BigDecimal("5000"));
        toAccount.setStatus(AccountStatus.ACTIVE);
        toAccount.setCurrency(Currency.CNY);
        
        testTransaction = new Transaction();
        testTransaction.setId("TXN20241225TEST001");
        testTransaction.setType(TransactionType.TRANSFER);
        testTransaction.setAmount(new BigDecimal("1000"));
        testTransaction.setFromAccount(fromAccount);
        testTransaction.setToAccount(toAccount);
        testTransaction.setRemark("测试转账");
        testTransaction.setIdempotencyKey("IDM123456");
        testTransaction.setReferenceNumber("REF123456");
        testTransaction.setStatus(TransactionStatus.PENDING);
        testTransaction.setTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建交易 - 成功场景")
    void testCreateTransaction_Success() {
        // Given
        when(transactionToolService.generateBankTransactionId()).thenReturn("TXN20241225TEST001");
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);
        doNothing().when(transactionToolService).performPostTransactionProcessing(any(), eq("CREATE"));
        
        // When
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then
        assertNotNull(result);
        assertEquals("TXN20241225TEST001", result.getId());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        assertEquals("OUT", result.getDirection());
        verify(transactionDao).save(any(Transaction.class));
        verify(transactionToolService).performPostTransactionProcessing(result, "CREATE");
    }

    @Test
    @DisplayName("创建交易 - 账户状态异常")
    void testCreateTransaction_InvalidAccountStatus() {
        // Given
        fromAccount.setStatus(AccountStatus.FROZEN);
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.INVALID_ACCOUNT, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("转出账户状态异常"));
        verify(transactionDao, never()).save(any());
    }

    @Test
    @DisplayName("创建交易 - 余额不足")
    void testCreateTransaction_InsufficientBalance() {
        // Given
        fromAccount.setBalance(new BigDecimal("500")); // 余额小于交易金额1000
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.INSUFFICIENT_BALANCE, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("账户余额不足"));
        verify(transactionDao, never()).save(any());
    }

    @Test
    @DisplayName("创建交易 - 超过单笔限额")
    void testCreateTransaction_ExceedsLimit() {
        // Given
        testTransaction.setAmount(new BigDecimal("60000")); // 超过限额50000
        fromAccount.setBalance(new BigDecimal("70000")); // 确保余额充足
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("单笔交易金额不能超过限额"));
        verify(transactionDao, never()).save(any());
    }

    @Test
    @DisplayName("创建交易 - 低于最小金额")
    void testCreateTransaction_BelowMinimum() {
        // Given
        testTransaction.setAmount(new BigDecimal("0.005")); // 低于最小金额0.01
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("交易金额不能小于最小限额"));
        verify(transactionDao, never()).save(any());
    }

    @Test
    @DisplayName("创建交易 - 存款类型配置")
    void testCreateTransaction_DepositType() {
        // Given
        testTransaction.setType(TransactionType.DEPOSIT);
        testTransaction.setFromAccount(null);
        when(transactionToolService.generateBankTransactionId()).thenReturn("TXN20241225TEST001");
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);
        
        // When
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then
        assertEquals("IN", result.getDirection());
        assertNull(result.getFromAccount());
        verify(transactionDao).save(any(Transaction.class));
    }

    @Test
    @DisplayName("创建交易 - 取款类型配置")
    void testCreateTransaction_WithdrawalType() {
        // Given
        testTransaction.setType(TransactionType.WITHDRAWAL);
        testTransaction.setToAccount(null);
        when(transactionToolService.generateBankTransactionId()).thenReturn("TXN20241225TEST001");
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);
        
        // When
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then
        assertEquals("OUT", result.getDirection());
        assertNull(result.getToAccount());
        verify(transactionDao).save(any(Transaction.class));
    }

    @Test
    @DisplayName("创建交易 - 转账缺少必要账户")
    void testCreateTransaction_TransferMissingAccount() {
        // Given
        testTransaction.setFromAccount(null); // 转账缺少转出账户
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("转账交易必须包含转出账户和转入账户"));
    }

    @Test
    @DisplayName("更新交易 - 成功场景（只允许修改remark）")
    void testUpdateTransaction_Success() {
        // Given
        String transactionId = "TXN20241225TEST001";
        Transaction updateData = new Transaction();
        updateData.setRemark("更新后的备注"); // 只修改remark字段
        
        when(transactionDao.findById(transactionId)).thenReturn(Optional.of(testTransaction));
        when(transactionDao.save(any(Transaction.class))).thenReturn(testTransaction);
        doNothing().when(transactionToolService).performPostTransactionProcessing(any(), eq("UPDATE"));
        
        // When
        Transaction result = transactionService.updateTransaction(transactionId, updateData);
        
        // Then
        assertNotNull(result);
        verify(transactionDao).save(any(Transaction.class));
        verify(transactionToolService).performPostTransactionProcessing(result, "UPDATE");
    }

    @Test
    @DisplayName("更新交易 - 交易不存在")
    void testUpdateTransaction_NotFound() {
        // Given
        String transactionId = "NONEXISTENT";
        when(transactionDao.findById(transactionId)).thenReturn(Optional.empty());
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.updateTransaction(transactionId, new Transaction()));
        
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
        verify(transactionDao, never()).save(any());
    }

    @Test
    @DisplayName("更新交易 - 已完成的交易不允许修改")
    void testUpdateTransaction_CompletedTransaction() {
        // Given
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        when(transactionDao.findById(anyString())).thenReturn(Optional.of(testTransaction));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.updateTransaction("TXN001", new Transaction()));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("已完成的交易不允许修改"));
    }

    @Test
    @DisplayName("更新交易 - 只更新remark字段，其他字段被忽略")
    void testUpdateTransaction_OnlyRemarkUpdated() {
        // Given
        Transaction updateData = new Transaction();
        updateData.setId("DIFFERENT_ID"); // 尝试修改ID，但会被忽略
        updateData.setAmount(new BigDecimal("9999")); // 尝试修改金额，但会被忽略
        updateData.setRemark("新的备注"); // 只有这个会被更新
        
        when(transactionDao.findById(anyString())).thenReturn(Optional.of(testTransaction));
        when(transactionDao.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(transactionToolService).performPostTransactionProcessing(any(), eq("UPDATE"));
        
        // When
        Transaction result = transactionService.updateTransaction("TXN001", updateData);
        
        // Then
        assertNotNull(result);
        assertEquals(testTransaction.getId(), result.getId()); // ID保持不变
        assertEquals(testTransaction.getAmount(), result.getAmount()); // 金额保持不变
        assertEquals("新的备注", result.getRemark()); // 只有remark被更新
        verify(transactionDao).save(any(Transaction.class));
    }

    @Test
    @DisplayName("更新交易 - 金额修改被忽略")
    void testUpdateTransaction_AmountChangeIgnored() {
        // Given
        Transaction updateData = new Transaction();
        updateData.setAmount(new BigDecimal("2000")); // 尝试修改金额，但会被忽略
        updateData.setRemark("测试备注"); // 只有remark会被更新
        
        when(transactionDao.findById(anyString())).thenReturn(Optional.of(testTransaction));
        when(transactionDao.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(transactionToolService).performPostTransactionProcessing(any(), eq("UPDATE"));
        
        // When
        Transaction result = transactionService.updateTransaction("TXN001", updateData);
        
        // Then - 金额保持不变，只有remark被更新
        assertNotNull(result);
        assertEquals(testTransaction.getAmount(), result.getAmount()); // 金额保持不变
        assertEquals("测试备注", result.getRemark()); // remark被更新
        verify(transactionDao).save(any(Transaction.class));
    }

    @Test
    @DisplayName("删除交易 - 银行业务规则禁止删除")
    void testDeleteTransaction_BusinessRuleProhibits() {
        // Given
        String transactionId = "TXN20241225TEST001";
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.deleteTransaction(transactionId));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("银行交易记录不允许删除"));
        // 验证没有调用数据库操作
        verify(transactionDao, never()).save(any());
        verify(transactionDao, never()).findById(any());
    }

    // 注意：删除交易的其他测试场景已移除，因为银行业务规则禁止删除任何交易记录

    @Test
    @DisplayName("查询交易 - 成功场景")
    void testGetTransactionById_Success() {
        // Given
        String transactionId = "TXN20241225TEST001";
        when(transactionDao.findById(transactionId)).thenReturn(Optional.of(testTransaction));
        
        // When
        Optional<Transaction> result = transactionService.getTransactionById(transactionId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testTransaction.getId(), result.get().getId());
        verify(transactionDao).findById(transactionId);
    }

    @Test
    @DisplayName("查询交易 - 交易不存在")
    void testGetTransactionById_NotFound() {
        // Given
        String transactionId = "NONEXISTENT";
        when(transactionDao.findById(transactionId)).thenReturn(Optional.empty());
        
        // When
        Optional<Transaction> result = transactionService.getTransactionById(transactionId);
        
        // Then
        assertFalse(result.isPresent());
        verify(transactionDao).findById(transactionId);
    }

    @Test
    @DisplayName("查询所有交易 - 成功场景")
    void testGetAllTransactions_Success() {
        // Given
        List<String> transactionIds = Arrays.asList("TXN001", "TXN002");
        
        when(transactionToolService.getTransactionIdsByTime(0, 10)).thenReturn(transactionIds);
        when(transactionToolService.getTotalTransactionCount()).thenReturn(2);
        when(transactionDao.findById("TXN001")).thenReturn(Optional.of(testTransaction));
        when(transactionDao.findById("TXN002")).thenReturn(Optional.of(testTransaction));
        
        // When
        PageInfo<Transaction> result = transactionService.getAllTransactions(0, 10);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotal());
        verify(transactionToolService).getTransactionIdsByTime(0, 10);
        verify(transactionToolService).getTotalTransactionCount();
    }

    @Test
    @DisplayName("系统异常处理 - 创建交易")
    void testCreateTransaction_SystemError() {
        // Given
        when(transactionToolService.generateBankTransactionId()).thenReturn("TXN001");
        when(transactionDao.save(any())).thenThrow(new RuntimeException("数据库连接失败"));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.createTransaction(testTransaction));
        
        assertEquals(ErrorCode.SYSTEM_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("创建交易失败"));
    }

    @Test
    @DisplayName("系统异常处理 - 更新交易")
    void testUpdateTransaction_SystemError() {
        // Given
        when(transactionDao.findById(anyString())).thenReturn(Optional.of(testTransaction));
        when(transactionDao.save(any())).thenThrow(new RuntimeException("数据库连接失败"));
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.updateTransaction("TXN001", new Transaction()));
        
        assertEquals(ErrorCode.SYSTEM_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("更新交易失败"));
    }

    @Test
    @DisplayName("系统异常处理 - 删除交易（业务规则禁止）")
    void testDeleteTransaction_SystemError() {
        // Given - 删除操作被业务规则禁止，不会到达系统层面
        
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> transactionService.deleteTransaction("TXN001"));
        
        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("银行交易记录不允许删除"));
    }
} 