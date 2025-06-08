package com.bank.transaction.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.TransactionType;

@ExtendWith(MockitoExtension.class)
@DisplayName("交易工具服务实现类单元测试")
class TransactionToolServiceImplTest {

    @InjectMocks
    private TransactionToolServiceImpl transactionToolService;
    
    private Transaction testTransaction;
    
    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId("TXN20241225TEST001");
        testTransaction.setType(TransactionType.TRANSFER);
        testTransaction.setAmount(new BigDecimal("1000"));
        testTransaction.setRemark("测试转账");
        testTransaction.setStatus(TransactionStatus.PENDING);
        testTransaction.setTimestamp(LocalDateTime.now());
    }

    @Test
    @DisplayName("生成银行交易ID - 格式正确")
    void testGenerateBankTransactionId() {
        // When
        String transactionId = transactionToolService.generateBankTransactionId();
        
        // Then
        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("TXN"));
        assertTrue(transactionId.length() >= 15); // TXN + 8位日期 + 8位随机
        
        // 验证日期部分格式
        String datePrefix = transactionId.substring(3, 11);
        assertTrue(datePrefix.matches("\\d{8}")); // YYYYMMDD格式
        
        // 验证随机部分格式
        String randomSuffix = transactionId.substring(11);
        assertTrue(randomSuffix.matches("[A-Z0-9]{8}")); // 8位大写字母数字
    }

    @Test
    @DisplayName("生成交易参考号 - 格式正确")
    void testGenerateReferenceNumber() {
        // When
        String refNumber = transactionToolService.generateReferenceNumber();
        
        // Then
        assertNotNull(refNumber);
        assertTrue(refNumber.startsWith("REF"));
        assertTrue(refNumber.length() >= 21); // REF + 14位时间戳 + 6位随机
        
        // 验证时间戳部分格式
        String timePrefix = refNumber.substring(3, 17);
        assertTrue(timePrefix.matches("\\d{14}")); // YYYYMMDDHHMMSS格式
        
        // 验证随机部分格式
        String randomSuffix = refNumber.substring(17);
        assertTrue(randomSuffix.matches("[A-Z0-9]{6}")); // 6位大写字母数字
    }

    @Test
    @DisplayName("生成幂等性键 - 格式正确")
    void testGenerateIdempotencyKey() {
        // When
        String idempotencyKey = transactionToolService.generateIdempotencyKey();
        
        // Then
        assertNotNull(idempotencyKey);
        assertTrue(idempotencyKey.startsWith("IDM"));
        assertTrue(idempotencyKey.length() >= 21); // IDM + 13位时间戳 + 8位随机
        
        // 验证时间戳部分（毫秒级）
        String timestamp = idempotencyKey.substring(3, idempotencyKey.length() - 8);
        assertTrue(timestamp.matches("\\d{13}")); // 13位毫秒时间戳
        
        // 验证随机部分格式
        String randomSuffix = idempotencyKey.substring(idempotencyKey.length() - 8);
        assertTrue(randomSuffix.matches("[A-Z0-9]{8}")); // 8位大写字母数字
    }

    @Test
    @DisplayName("生成器唯一性测试 - 交易ID")
    void testGenerateBankTransactionId_Uniqueness() {
        // When
        String id1 = transactionToolService.generateBankTransactionId();
        String id2 = transactionToolService.generateBankTransactionId();
        
        // Then
        assertNotEquals(id1, id2, "连续生成的交易ID应该不同");
    }

    @Test
    @DisplayName("生成器唯一性测试 - 参考号")
    void testGenerateReferenceNumber_Uniqueness() {
        // When
        String ref1 = transactionToolService.generateReferenceNumber();
        String ref2 = transactionToolService.generateReferenceNumber();
        
        // Then
        assertNotEquals(ref1, ref2, "连续生成的参考号应该不同");
    }

    @Test
    @DisplayName("生成器唯一性测试 - 幂等性键")
    void testGenerateIdempotencyKey_Uniqueness() {
        // When
        String key1 = transactionToolService.generateIdempotencyKey();
        String key2 = transactionToolService.generateIdempotencyKey();
        
        // Then
        assertNotEquals(key1, key2, "连续生成的幂等性键应该不同");
    }

    @Test
    @DisplayName("银行后续处理 - CREATE操作")
    void testPerformPostTransactionProcessing_Create() {
        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> 
            transactionToolService.performPostTransactionProcessing(testTransaction, "CREATE"));
    }

    @Test
    @DisplayName("银行后续处理 - UPDATE操作")
    void testPerformPostTransactionProcessing_Update() {
        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> 
            transactionToolService.performPostTransactionProcessing(testTransaction, "UPDATE"));
    }

    @Test
    @DisplayName("银行后续处理 - DELETE操作")
    void testPerformPostTransactionProcessing_Delete() {
        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> 
            transactionToolService.performPostTransactionProcessing(testTransaction, "DELETE"));
    }

    @Test
    @DisplayName("银行后续处理 - 空交易对象")
    void testPerformPostTransactionProcessing_NullTransaction() {
        // When & Then - 应该不抛出异常（方法内部有异常处理）
        assertDoesNotThrow(() -> 
            transactionToolService.performPostTransactionProcessing(null, "CREATE"));
    }

    @Test
    @DisplayName("银行后续处理 - 空操作类型")
    void testPerformPostTransactionProcessing_NullOperationType() {
        // When & Then - 应该不抛出异常（方法内部有异常处理）
        assertDoesNotThrow(() -> 
            transactionToolService.performPostTransactionProcessing(testTransaction, null));
    }

    @Test
    @DisplayName("生成多个交易ID性能测试")
    void testGenerateBankTransactionId_Performance() {
        // Given
        int count = 1000;
        long startTime = System.currentTimeMillis();
        
        // When
        for (int i = 0; i < count; i++) {
            transactionToolService.generateBankTransactionId();
        }
        
        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 1000次生成应该在合理时间内完成（例如1秒）
        assertTrue(duration < 1000, "生成1000个交易ID应该在1秒内完成，实际耗时: " + duration + "ms");
    }

    @Test
    @DisplayName("大批量生成唯一性验证")
    void testBatchGeneration_Uniqueness() {
        // Given
        int batchSize = 100;
        java.util.Set<String> generatedIds = new java.util.HashSet<>();
        
        // When
        for (int i = 0; i < batchSize; i++) {
            String id = transactionToolService.generateBankTransactionId();
            generatedIds.add(id);
        }
        
        // Then
        assertEquals(batchSize, generatedIds.size(), "批量生成的ID应该全部唯一");
    }
} 