package com.bank.transaction.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "银行实体")
public class Bank {
    @NotBlank(message = "银行ID不能为空")
    @Size(max = 32, message = "银行ID长度不能超过32字符")
    @Schema(description = "银行唯一标识", example = "BANK001")
    private String bankId; // 银行ID，不能为空
    
    @NotBlank(message = "银行名称不能为空")
    @Size(max = 100, message = "银行名称长度不能超过100字符")
    @Schema(description = "银行名称", example = "中国工商银行")
    private String bankName; // 银行名称，不能为空

    public Bank() {}

    public Bank(String bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
    }

    public String getBankId() {return bankId;}
    public void setBankId(String bankId) {this.bankId = bankId;}
    public String getBankName() {return bankName;}
    public void setBankName(String bankName) {this.bankName = bankName;}
} 