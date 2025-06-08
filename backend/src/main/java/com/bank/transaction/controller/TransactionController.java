package com.bank.transaction.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import com.bank.transaction.util.PageInfo;
import com.bank.transaction.exception.business.ValidationException;
import com.bank.transaction.util.Constants.ErrorCode;
import com.bank.transaction.util.Constants.TransactionType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * 交易管理控制器
 * 
 * 特别说明：
 * - 为了兼容旧设备和网络设备，统一使用 POST 方法
 * - 部分旧设备/代理不支持 PUT/DELETE 方法
 * - 通过不同的路径区分操作类型：/create, /update, /delete
 * - 金额验证通过Bean Validation注解在Transaction实体上处理
 */
@Validated
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}) // 生产环境应限制具体域名
@RequiredArgsConstructor
@Tag(name = "交易管理", description = "交易的创建、查询、更新和删除（统一使用POST方法兼容旧设备）")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/create")
    @Operation(summary = "创建交易", description = "新增一笔交易记录")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        // 复杂业务校验（金额验证现在由Bean Validation处理）
        validateTransactionBusiness(transaction);
        
        Transaction result = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/update")
    @Operation(summary = "修改交易", description = "根据交易ID修改指定交易内容，需要在请求体中包含交易ID")
    public ResponseEntity<Transaction> updateTransaction(@Valid @RequestBody UpdateTransactionRequest request) {
        // 注意：这里不对transaction对象进行业务校验，因为只允许修改remark字段
        // 具体的字段限制在Service层进行验证
        
        Transaction result = transactionService.updateTransaction(request.getId(), request.getTransaction());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/delete")
    @Operation(summary = "删除交易", description = "根据交易ID删除指定交易")
    public ResponseEntity<Void> deleteTransaction(@Valid @RequestBody DeleteTransactionRequest request) {
        transactionService.deleteTransaction(request.getId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}")
    @Operation(summary = "查询交易详情", description = "根据交易ID查询交易记录详情")
    public ResponseEntity<Transaction> getTransactionById(
            @PathVariable @NotBlank(message = "交易ID不能为空") String id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "列出所有交易", description = "分页展示所有交易记录")
    public ResponseEntity<PageInfo<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "页码不能小于0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页大小必须大于0") @Max(value = 100, message = "每页大小不能超过100") int size) {
        PageInfo<Transaction> result = transactionService.getAllTransactions(page, size);
        return ResponseEntity.ok(result);
    }
    
    // 复杂业务校验方法
    private void validateTransactionBusiness(Transaction transaction) {
        // 1. 防止自转账（转出账户ID不能相同）
        if (transaction.getFromAccountId() != null && transaction.getToAccountId() != null 
            && transaction.getFromAccountId().equals(transaction.getToAccountId())) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "转出账户和转入账户不能相同");
        }
        
        // 2. 根据交易类型校验账户信息
        if (transaction.getType() == TransactionType.TRANSFER 
            && (transaction.getFromAccountId() == null || transaction.getToAccountId() == null)) {
            throw new ValidationException(ErrorCode.VALIDATION_ERROR, "转账交易必须包含转出账户ID和转入账户ID");
        }
        
        // 注意：金额范围校验现在由Bean Validation注解在Transaction实体上处理
    }
    
    /**
     * 更新交易请求对象
     */
    public static class UpdateTransactionRequest {
        @NotBlank(message = "交易ID不能为空")
        private String id;
        
        private Transaction transaction;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public Transaction getTransaction() { return transaction; }
        public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    }
    
    /**
     * 删除交易请求对象
     */
    public static class DeleteTransactionRequest {
        @NotBlank(message = "交易ID不能为空")
        private String id;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
} 