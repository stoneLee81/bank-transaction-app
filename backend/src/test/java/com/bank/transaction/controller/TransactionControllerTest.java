package com.bank.transaction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import com.bank.transaction.util.Constants.Currency;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.ErrorCode;
import com.bank.transaction.util.PageInfo;
import com.bank.transaction.exception.business.ValidationException;
import com.bank.transaction.exception.business.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TransactionController 单元测试
 */
@WebMvcTest(TransactionController.class)
@DisplayName("交易控制器测试")
class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService transactionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Transaction sampleTransaction;
    
    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction();
        sampleTransaction.setId("TXN001");
        sampleTransaction.setAmount(new BigDecimal("1000.00"));
        sampleTransaction.setType(TransactionType.TRANSFER);
        sampleTransaction.setRemark("测试转账");
        sampleTransaction.setCurrency(Currency.CNY);
        sampleTransaction.setChannel("ONLINE");
        sampleTransaction.setFromAccountId("ACC001");
        sampleTransaction.setToAccountId("ACC002");
        sampleTransaction.setTimestamp(LocalDateTime.now());
        sampleTransaction.setStatus(TransactionStatus.PENDING);
        sampleTransaction.setDirection("OUT");
    }
    
    @Test
    @DisplayName("创建交易 - 成功")
    void createTransaction_Success() throws Exception {
        // Given
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(sampleTransaction);
        
        // When & Then
        mockMvc.perform(post("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("TXN001"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.fromAccountId").value("ACC001"))
                .andExpect(jsonPath("$.toAccountId").value("ACC002"));
    }
    
    @Test
    @DisplayName("创建交易 - 自转账校验失败")
    void createTransaction_SelfTransfer_Fail() throws Exception {
        // Given
        Transaction selfTransferTransaction = new Transaction();
        selfTransferTransaction.setAmount(new BigDecimal("1000.00"));
        selfTransferTransaction.setType(TransactionType.TRANSFER);
        selfTransferTransaction.setRemark("自转账测试");
        selfTransferTransaction.setCurrency(Currency.CNY);
        selfTransferTransaction.setChannel("ONLINE");
        selfTransferTransaction.setFromAccountId("ACC001");
        selfTransferTransaction.setToAccountId("ACC001"); // 相同账户ID
        
        // When & Then
        mockMvc.perform(post("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(selfTransferTransaction)))
                .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    @DisplayName("创建交易 - 转账缺少账户ID校验失败")
    void createTransaction_TransferMissingAccount_Fail() throws Exception {
        // Given
        Transaction missingAccountTransaction = new Transaction();
        missingAccountTransaction.setAmount(new BigDecimal("1000.00"));
        missingAccountTransaction.setType(TransactionType.TRANSFER);
        missingAccountTransaction.setRemark("缺少账户ID");
        missingAccountTransaction.setCurrency(Currency.CNY);
        missingAccountTransaction.setChannel("ONLINE");
        missingAccountTransaction.setFromAccountId("ACC001");
        // 缺少 toAccountId
        
        // When & Then
        mockMvc.perform(post("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(missingAccountTransaction)))
                .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    @DisplayName("创建交易 - 金额超限校验失败")
    void createTransaction_AmountExceedsLimit_Fail() throws Exception {
        // Given
        Transaction largAmountTransaction = new Transaction();
        largAmountTransaction.setAmount(new BigDecimal("60000.00")); // 超过50000限额
        largAmountTransaction.setType(TransactionType.TRANSFER);
        largAmountTransaction.setRemark("大额转账");
        largAmountTransaction.setCurrency(Currency.CNY);
        largAmountTransaction.setChannel("ONLINE");
        largAmountTransaction.setFromAccountId("ACC001");
        largAmountTransaction.setToAccountId("ACC002");
        
        // Mock service层抛出业务异常
        when(transactionService.createTransaction(any(Transaction.class)))
            .thenThrow(new BusinessException(ErrorCode.VALIDATION_ERROR, "交易金额不能超过单笔限额: 50000"));
        
        // When & Then
        mockMvc.perform(post("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largAmountTransaction)))
                .andExpect(status().isBadRequest()); // BusinessException 返回400
    }
    
    @Test
    @DisplayName("查询交易 - 成功")
    void getTransactionById_Success() throws Exception {
        // Given
        when(transactionService.getTransactionById("TXN001")).thenReturn(Optional.of(sampleTransaction));
        
        // When & Then
        mockMvc.perform(post("/api/transactions/TXN001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("TXN001"))
                .andExpect(jsonPath("$.amount").value(1000.00));
    }
    
    @Test
    @DisplayName("查询交易 - 不存在")
    void getTransactionById_NotFound() throws Exception {
        // Given
        when(transactionService.getTransactionById("INVALID")).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(post("/api/transactions/INVALID"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("分页查询交易 - 成功")
    void getAllTransactions_Success() throws Exception {
        // Given
        PageInfo<Transaction> pageInfo = PageInfo.of(Arrays.asList(sampleTransaction), 0, 20, 1);
        
        when(transactionService.getAllTransactions(0, 20)).thenReturn(pageInfo);
        
        // When & Then
        mockMvc.perform(post("/api/transactions")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value("TXN001"))
                .andExpect(jsonPath("$.total").value(1));
    }
    
    @Test
    @DisplayName("更新交易 - 成功")
    void updateTransaction_Success() throws Exception {
        // Given
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId("TXN001");
        updatedTransaction.setAmount(new BigDecimal("1500.00"));
        updatedTransaction.setType(TransactionType.TRANSFER);
        updatedTransaction.setRemark("更新后的转账");
        updatedTransaction.setCurrency(Currency.CNY);
        updatedTransaction.setChannel("ONLINE");
        updatedTransaction.setFromAccountId("ACC001");
        updatedTransaction.setToAccountId("ACC002");
        
        TransactionController.UpdateTransactionRequest request = new TransactionController.UpdateTransactionRequest();
        request.setId("TXN001");
        request.setTransaction(updatedTransaction);
        
        when(transactionService.updateTransaction(anyString(), any(Transaction.class))).thenReturn(updatedTransaction);
        
        // When & Then
        mockMvc.perform(post("/api/transactions/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.remark").value("更新后的转账"));
    }
    
    @Test
    @DisplayName("删除交易 - 银行业务规则禁止")
    void deleteTransaction_BusinessRuleProhibits() throws Exception {
        // Given
        TransactionController.DeleteTransactionRequest request = new TransactionController.DeleteTransactionRequest();
        request.setId("TXN001");
        
        doThrow(new ValidationException(ErrorCode.VALIDATION_ERROR, "银行交易记录不允许删除，如需处理请联系管理员"))
            .when(transactionService).deleteTransaction("TXN001");
        
        // When & Then
        mockMvc.perform(post("/api/transactions/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    @DisplayName("参数校验 - 缺少必填字段")
    void validation_MissingRequiredFields() throws Exception {
        // Given
        Transaction invalidTransaction = new Transaction();
        // 缺少必填字段
        
        // When & Then
        mockMvc.perform(post("/api/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransaction)))
                .andExpect(status().isBadRequest());
    }
} 