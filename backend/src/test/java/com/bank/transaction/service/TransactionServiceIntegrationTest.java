package com.bank.transaction.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.bank.transaction.model.Account;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.AccountStatus;
import com.bank.transaction.util.Constants.Currency;

/**
 * 交易服务集成测试
 * 测试 TransactionService 和 TransactionToolService 之间的协作
 */
@SpringBootTest(classes = com.bank.transaction.BankTransactionApplication.class)
@ActiveProfiles("test")
@DisplayName("银行交易服务集成测试")
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private TransactionToolService transactionToolService;
    
    private Transaction testTransaction;
    private Account fromAccount;
    private Account toAccount;
    
    @BeforeEach
    void setUp() {
        // 准备测试数据
        fromAccount = new Account();
        fromAccount.setAccountNumber("6222021234567890");
        fromAccount.setBalance(new BigDecimal("10000"));
        fromAccount.setStatus(AccountStatus.ACTIVE);
        fromAccount.setCurrency(Currency.CNY);
        
        toAccount = new Account();
        toAccount.setAccountNumber("6222021234567891");
        toAccount.setBalance(new BigDecimal("5000"));
        toAccount.setStatus(AccountStatus.ACTIVE);
        toAccount.setCurrency(Currency.CNY);
        
        testTransaction = new Transaction();
        testTransaction.setType(TransactionType.TRANSFER);
        testTransaction.setAmount(new BigDecimal("1000"));
        testTransaction.setFromAccount(fromAccount);
        testTransaction.setToAccount(toAccount);
        testTransaction.setRemark("集成测试转账");
    }

    @Test
    @DisplayName("集成测试 - 完整的创建交易流程")
    void testFullCreateTransactionFlow() {
        // When - 执行完整的创建流程
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then - 验证所有组件协作结果
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId().startsWith("TXN"));
        assertNotNull(result.getReferenceNumber());
        assertTrue(result.getReferenceNumber().startsWith("REF"));
        assertNotNull(result.getIdempotencyKey());
        assertTrue(result.getIdempotencyKey().startsWith("IDM"));
        // 注意：由于银行核心系统的后续处理，状态会从PENDING自动更新为COMPLETED
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals("OUT", result.getDirection());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("集成测试 - TransactionToolService 生成器组合使用")
    void testTransactionToolServiceCombination() {
        // When - 使用工具服务生成各种标识符
        String transactionId = transactionToolService.generateBankTransactionId();
        String referenceNumber = transactionToolService.generateReferenceNumber();
        String idempotencyKey = transactionToolService.generateIdempotencyKey();
        
        // Then - 验证生成的标识符格式和唯一性
        assertNotNull(transactionId);
        assertNotNull(referenceNumber);
        assertNotNull(idempotencyKey);
        
        assertTrue(transactionId.startsWith("TXN"));
        assertTrue(referenceNumber.startsWith("REF"));
        assertTrue(idempotencyKey.startsWith("IDM"));
        
        // 验证都不相同
        assertNotEquals(transactionId.substring(3), referenceNumber.substring(3));
        assertNotEquals(transactionId.substring(3), idempotencyKey.substring(3));
        assertNotEquals(referenceNumber.substring(3), idempotencyKey.substring(3));
    }

    @Test
    @DisplayName("集成测试 - 存款交易完整流程")
    void testDepositTransactionIntegration() {
        // Given
        testTransaction.setType(TransactionType.DEPOSIT);
        testTransaction.setFromAccount(null);
        
        // When
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then
        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals("IN", result.getDirection());
        assertNull(result.getFromAccount());
        assertNotNull(result.getToAccount());
    }

    @Test
    @DisplayName("集成测试 - 取款交易完整流程")
    void testWithdrawalTransactionIntegration() {
        // Given
        testTransaction.setType(TransactionType.WITHDRAWAL);
        testTransaction.setToAccount(null);
        
        // When
        Transaction result = transactionService.createTransaction(testTransaction);
        
        // Then
        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals("OUT", result.getDirection());
        assertNotNull(result.getFromAccount());
        assertNull(result.getToAccount());
    }

    @Test
    @DisplayName("集成测试 - 后续处理消息推送")
    void testPostTransactionProcessing() {
        // Given
        Transaction savedTransaction = transactionService.createTransaction(testTransaction);
        
        // When & Then - 后续处理应该不抛出异常
        assertDoesNotThrow(() -> {
            transactionToolService.performPostTransactionProcessing(savedTransaction, "CREATE");
            transactionToolService.performPostTransactionProcessing(savedTransaction, "UPDATE");
            transactionToolService.performPostTransactionProcessing(savedTransaction, "DELETE");
        });
    }

    @Test
    @DisplayName("集成测试 - 高并发生成唯一标识符")
    void testConcurrentIdGeneration() {
        // Given
        int threadCount = 10;
        int generationsPerThread = 50;
        java.util.Set<String> allGeneratedIds = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        
        // When - 多线程并发生成
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < generationsPerThread; j++) {
                        String id = transactionToolService.generateBankTransactionId();
                        allGeneratedIds.add(id);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // Then - 等待所有线程完成并验证唯一性
        assertDoesNotThrow(() -> latch.await());
        assertEquals(threadCount * generationsPerThread, allGeneratedIds.size(), 
            "并发生成的所有ID应该都是唯一的");
    }

    @Test
    @DisplayName("集成测试 - 性能基准测试")
    void testPerformanceBenchmark() {
        // Given
        int iterations = 1000;
        long startTime = System.currentTimeMillis();
        
        // When - 连续创建多个交易
        for (int i = 0; i < iterations; i++) {
            Transaction transaction = new Transaction();
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setAmount(new BigDecimal("100"));
            transaction.setToAccount(toAccount);
            transaction.setRemark("性能测试存款 " + i);
            
            transactionService.createTransaction(transaction);
        }
        
        // Then - 验证性能
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 应该在合理时间内完成（例如10秒）
        assertTrue(duration < 10000, 
            "创建" + iterations + "个交易应该在10秒内完成，实际耗时: " + duration + "ms");
        
        // 平均每个交易创建时间应该在合理范围内
        double avgTimePerTransaction = (double) duration / iterations;
        assertTrue(avgTimePerTransaction < 100, 
            "平均每个交易创建时间应该小于100ms，实际: " + avgTimePerTransaction + "ms");
    }
} 