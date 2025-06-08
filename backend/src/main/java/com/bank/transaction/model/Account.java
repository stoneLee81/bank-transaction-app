package com.bank.transaction.model;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import com.bank.transaction.util.Constants.AccountStatus;
import com.bank.transaction.util.Constants.Currency;

import java.math.BigDecimal;

@Schema(description = "账户实体")
public class Account {
    @NotBlank(message = "账户ID不能为空")
    @Size(max = 64, message = "账户ID长度不能超过64字符")
    @Schema(description = "账户唯一标识", example = "ACC123456")
    private String accountId; // 账户ID，不能为空
    
    @NotBlank(message = "账户号码不能为空")
    @Size(max = 32, message = "账户号码长度不能超过32字符")
    @Schema(description = "账户号码", example = "6214830001234567")
    private String accountNumber; // 账户号码，不能为空
    
    @NotBlank(message = "账户名称不能为空")
    @Size(max = 100, message = "账户名称长度不能超过100字符")
    @Schema(description = "账户名称", example = "张三")
    private String accountName; // 账户名称，不能为空
    
    @NotNull(message = "所属银行不能为空")
    @Valid
    @Schema(description = "所属银行")
    private Bank bank; // 所属银行，不能为空

    @Schema(description = "账户余额", example = "10000.50")
    @NotNull(message = "账户余额不能为空")
    @PositiveOrZero(message = "账户余额不能为负数")
    private BigDecimal balance;

    @Schema(description = "账户货币类型", example = "CNY")
    @NotNull(message = "账户币种不能为空")
    private Currency currency;

    @Schema(description = "账户状态", example = "ACTIVE")
    @NotNull(message = "账户状态不能为空")
    private AccountStatus status;

    public Account() {}

    public Account(String accountId, String accountNumber, String accountName, Bank bank, BigDecimal balance, Currency currency, AccountStatus status) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.bank = bank;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
    }

    public String getAccountId() {return accountId;}
    public void setAccountId(String accountId) {this.accountId = accountId;}
    public String getAccountNumber() {return accountNumber;}
    public void setAccountNumber(String accountNumber) {this.accountNumber = accountNumber;}
    public String getAccountName() {return accountName;}
    public void setAccountName(String accountName) {this.accountName = accountName;}
    public Bank getBank() {return bank;}
    public void setBank(Bank bank) {this.bank = bank;}
    public BigDecimal getBalance() {return balance;}
    public void setBalance(BigDecimal balance) {this.balance = balance;}
    public Currency getCurrency() {return currency;}
    public void setCurrency(Currency currency) {this.currency = currency;}
    public AccountStatus getStatus() {return status;}
    public void setStatus(AccountStatus status) {this.status = status;}
} 