package com.bank.transaction.validation;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.Currency;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("交易验证测试")
public class TransactionValidationTest {

    @Autowired
    private Validator validator;

    private Transaction baseTransaction;

    @BeforeEach
    void setUp() {
        baseTransaction = new Transaction();
        baseTransaction.setAmount(new BigDecimal("1000.00"));
        baseTransaction.setType(TransactionType.TRANSFER);
        baseTransaction.setRemark("测试交易");
        baseTransaction.setCurrency(Currency.CNY);
        baseTransaction.setChannel("ONLINE");
        baseTransaction.setFromAccountId("ACC001");
        baseTransaction.setToAccountId("ACC002");
    }

    @Test
    @DisplayName("备注字段 - null值应该通过验证")
    void testRemarkNull_ShouldPass() {
        baseTransaction.setRemark(null);
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出remark字段的违规
        long remarkViolations = violations.stream()
            .filter(v -> "remark".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, remarkViolations, "remark为null时不应该有验证错误");
    }

    @Test
    @DisplayName("备注字段 - 空字符串应该通过验证")
    void testRemarkEmpty_ShouldPass() {
        baseTransaction.setRemark("");
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出remark字段的违规
        long remarkViolations = violations.stream()
            .filter(v -> "remark".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, remarkViolations, "remark为空字符串时不应该有验证错误");
    }

    @Test
    @DisplayName("备注字段 - 正常长度应该通过验证")
    void testRemarkNormalLength_ShouldPass() {
        baseTransaction.setRemark("这是一个正常长度的备注");
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出remark字段的违规
        long remarkViolations = violations.stream()
            .filter(v -> "remark".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, remarkViolations, "正常长度的remark不应该有验证错误");
    }

    @Test
    @DisplayName("备注字段 - 255字符应该通过验证")
    void testRemark255Characters_ShouldPass() {
        String longRemark = "a".repeat(255);
        baseTransaction.setRemark(longRemark);
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出remark字段的违规
        long remarkViolations = violations.stream()
            .filter(v -> "remark".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, remarkViolations, "255字符的remark不应该有验证错误");
    }

    @Test
    @DisplayName("备注字段 - 超过255字符应该失败")
    void testRemarkTooLong_ShouldFail() {
        String tooLongRemark = "a".repeat(256);
        baseTransaction.setRemark(tooLongRemark);
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出remark字段的违规
        Set<ConstraintViolation<Transaction>> remarkViolations = violations.stream()
            .filter(v -> "remark".equals(v.getPropertyPath().toString()))
            .collect(java.util.stream.Collectors.toSet());
        
        assertEquals(1, remarkViolations.size(), "超过255字符的remark应该有验证错误");
        
        ConstraintViolation<Transaction> violation = remarkViolations.iterator().next();
        assertTrue(violation.getMessage().contains("备注长度不能超过255字符"), 
            "错误信息应该包含长度限制说明");
    }

    @Test
    @DisplayName("交易描述字段 - null值应该通过验证（修改后）")
    void testDescriptionNull_ShouldPass() {
        baseTransaction.setRemark(null);
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出description字段的违规
        long descriptionViolations = violations.stream()
            .filter(v -> "description".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, descriptionViolations, "description为null时不应该有验证错误");
    }

    @Test
    @DisplayName("交易描述字段 - 空字符串应该通过验证（修改后）")
    void testDescriptionEmpty_ShouldPass() {
        baseTransaction.setRemark("");
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出description字段的违规
        long descriptionViolations = violations.stream()
            .filter(v -> "description".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, descriptionViolations, "description为空字符串时不应该有验证错误");
    }

    @Test
    @DisplayName("金额验证 - 正常金额应该通过")
    void testAmountValid_ShouldPass() {
        baseTransaction.setAmount(new BigDecimal("1000.00"));
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出amount字段的违规
        long amountViolations = violations.stream()
            .filter(v -> "amount".equals(v.getPropertyPath().toString()))
            .count();
        
        assertEquals(0, amountViolations, "正常金额不应该有验证错误");
    }

    @Test
    @DisplayName("金额验证 - 超过限额（现在由Service层处理）")
    void testAmountTooLarge_ServiceLayerValidation() {
        baseTransaction.setAmount(new BigDecimal("60000.00"));
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出amount字段的违规 - 现在Bean Validation只检查@Positive
        Set<ConstraintViolation<Transaction>> amountViolations = violations.stream()
            .filter(v -> "amount".equals(v.getPropertyPath().toString()))
            .collect(java.util.stream.Collectors.toSet());
        
        // Bean Validation层面不应该有错误，因为金额是正数
        assertEquals(0, amountViolations.size(), "Bean Validation层面不应该有金额范围错误，现在由Service层处理");
    }

    @Test
    @DisplayName("金额验证 - 小于最小值（现在由Service层处理）")
    void testAmountTooSmall_ServiceLayerValidation() {
        baseTransaction.setAmount(new BigDecimal("0.001"));
        
        Set<ConstraintViolation<Transaction>> violations = validator.validate(baseTransaction);
        
        // 过滤出amount字段的违规 - 现在Bean Validation只检查@Positive
        Set<ConstraintViolation<Transaction>> amountViolations = violations.stream()
            .filter(v -> "amount".equals(v.getPropertyPath().toString()))
            .collect(java.util.stream.Collectors.toSet());
        
        // Bean Validation层面不应该有错误，因为金额是正数
        assertEquals(0, amountViolations.size(), "Bean Validation层面不应该有金额范围错误，现在由Service层处理");
    }
} 