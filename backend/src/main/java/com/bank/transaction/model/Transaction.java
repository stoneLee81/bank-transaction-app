package com.bank.transaction.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bank.transaction.util.Constants.TransactionType;
import com.bank.transaction.util.Constants.TransactionStatus;
import com.bank.transaction.util.Constants.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;

@Schema(description = "交易实体")
public class Transaction {
    @Size(max = 64, message = "交易ID长度不能超过64位")
    @Schema(description = "交易唯一标识（系统自动生成）", example = "TXN20241201ABCD1234", accessMode = Schema.AccessMode.READ_ONLY)
    private String id; // 交易唯一标识，创建时自动生成
    
    @NotNull(message = "交易金额不能为空")
//    @DecimalMin(value = "0.01", message = "交易金额不能小于0.01元")
//    @DecimalMax(value = "50000", message = "单笔交易金额不能超过50000元")
    @Positive(message = "交易金额必须大于0")
    @Schema(description = "交易金额", example = "1000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount; // 交易金额，范围由配置文件决定
    
    @NotNull(message = "交易类型不能为空")
    @Schema(description = "交易类型", example = "DEPOSIT", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransactionType type; // 交易类型，不能为空

    // @Size(min = 0, max = 255, message = "交易描述长度不能超过255字符")
    // @Schema(description = "交易描述", example = "银行转账", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    // private String description; // 交易描述，不能为空
    
    @Schema(description = "交易时间（系统自动生成）", example = "2024-01-01T12:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime timestamp; // 交易时间，创建时自动生成
    
    @Schema(description = "交易状态（系统自动生成）", example = "PENDING", accessMode = Schema.AccessMode.READ_ONLY)
    private TransactionStatus status; // 交易状态，创建时自动生成
    
    @NotNull(message = "货币类型不能为空")
    @Schema(description = "货币类型", example = "CNY", requiredMode = Schema.RequiredMode.REQUIRED)
    private Currency currency; // 货币类型，不能为空
    
    @NotBlank(message = "交易渠道不能为空")
    @Size(max = 32, message = "交易渠道长度不能超过32字符")
    @Schema(description = "交易渠道", example = "ONLINE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String channel; // 交易渠道，不能为空
    
    @JsonIgnore
    @Size(max = 64, message = "交易参考号长度不能超过64字符")
    @Schema(hidden = true)
    private String referenceNumber; // 交易参考号/流水号（敏感信息，不返回前端）
    
    @Size(max = 32, message = "交易方向长度不能超过32字符")
    @Schema(description = "交易方向（系统自动设置）", example = "IN", allowableValues = {"IN", "OUT"}, accessMode = Schema.AccessMode.READ_ONLY)
    private String direction; // 交易方向，系统根据交易类型自动设置
    
    @Size(min = 0, max = 255, message = "备注长度不能超过255字符")
    @Schema(description = "备注信息（可选）", example = "转账备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark; // 备注，可选
    
    @Size(max = 32, message = "转出账户ID长度不能超过32字符")
    @Schema(description = "转出账户ID", example = "ACC001")
    private String fromAccountId; // 转出账户ID
    
    @Size(max = 32, message = "转入账户ID长度不能超过32字符")
    @Schema(description = "转入账户ID", example = "ACC002")
    private String toAccountId; // 转入账户ID
    
    @JsonIgnore
    @Valid
    @Schema(hidden = true)
    private Account fromAccount; // 转出账户（敏感信息，不返回前端）
    
    @JsonIgnore
    @Valid
    @Schema(hidden = true)
    private Account toAccount; // 转入账户（敏感信息，不返回前端）

    @JsonIgnore
    @Size(max = 64, message = "幂等性标识不能超过64字符")
    @Schema(hidden = true)
    private String idempotencyKey; // 幂等性标识（敏感信息，不返回前端）

    @Size(max = 64, message = "发起人用户ID不能超过64字符")
    @Schema(description = "发起人用户ID", example = "USER123")
    private String initiatedBy;

    @Size(max = 64, message = "审核人用户ID不能超过64字符")
    @Schema(description = "审核人用户ID", example = "ADMIN456")
    private String approvedBy;
    
    public Transaction() {}

    public Transaction(String id, BigDecimal amount, TransactionType type,
                      LocalDateTime timestamp, TransactionStatus status, Currency currency, 
                      String channel, String referenceNumber, String direction, String remark, 
                      String fromAccountId, String toAccountId, Account fromAccount, Account toAccount, 
                      String idempotencyKey, String initiatedBy, String approvedBy) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.status = status;
        this.currency = currency;
        this.channel = channel;
        this.referenceNumber = referenceNumber;
        this.direction = direction;
        this.remark = remark;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.idempotencyKey = idempotencyKey;
        this.initiatedBy = initiatedBy;
        this.approvedBy = approvedBy;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}
    public TransactionType getType() {return type;}
    public void setType(TransactionType type) {this.type = type;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
    public TransactionStatus getStatus() {return status;}
    public void setStatus(TransactionStatus status) {this.status = status;}
    public Currency getCurrency() {return currency;}
    public void setCurrency(Currency currency) {this.currency = currency;}
    public String getChannel() {return channel;}
    public void setChannel(String channel) {this.channel = channel;}
    public String getReferenceNumber() {return referenceNumber;}
    public void setReferenceNumber(String referenceNumber) {this.referenceNumber = referenceNumber;}
    public String getDirection() {return direction;}
    public void setDirection(String direction) {this.direction = direction;}
    public String getRemark() {return remark;}
    public void setRemark(String remark) {this.remark = remark;}
    public String getFromAccountId() {return fromAccountId;}
    public void setFromAccountId(String fromAccountId) {this.fromAccountId = fromAccountId;}
    public String getToAccountId() {return toAccountId;}
    public void setToAccountId(String toAccountId) {this.toAccountId = toAccountId;}
    public Account getFromAccount() {return fromAccount;}
    public void setFromAccount(Account fromAccount) {this.fromAccount = fromAccount;}
    public Account getToAccount() {return toAccount;}
    public void setToAccount(Account toAccount) {this.toAccount = toAccount;}
    public String getIdempotencyKey() {return idempotencyKey;}
    public void setIdempotencyKey(String idempotencyKey) {this.idempotencyKey = idempotencyKey;}
    public String getInitiatedBy() {return initiatedBy;}
    public void setInitiatedBy(String initiatedBy) {this.initiatedBy = initiatedBy;}
    public String getApprovedBy() {return approvedBy;}
    public void setApprovedBy(String approvedBy) {this.approvedBy = approvedBy;}
} 