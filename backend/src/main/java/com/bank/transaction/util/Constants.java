package com.bank.transaction.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Constants {
    /**
     * 交易类型
     * DEPOSIT: 存款
     * WITHDRAWAL: 取款
     * TRANSFER: 转账
     * PAYMENT: 支付
     * REFUND: 退款
     */
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, REFUND;
        
        @JsonCreator
        public static TransactionType fromString(String key) {
            return key == null ? null : TransactionType.valueOf(key.toUpperCase());
        }
        
        @JsonValue
        public String toValue() {
            return this.name();
        }
    }
    
    /**
     * 交易状态
     * PENDING: 待处理
     * COMPLETED: 已完成
     * FAILED: 失败
     * CANCELLED: 已取消
     */
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED;
        
        @JsonCreator
        public static TransactionStatus fromString(String key) {
            return key == null ? null : TransactionStatus.valueOf(key.toUpperCase());
        }
        
        @JsonValue
        public String toValue() {
            return this.name();
        }
    }
    
    /**
     * 异常级别枚举
     * FATAL: 致命异常，系统不可用，需要立即告警
     * ERROR: 重要异常，影响业务功能
     * WARN: 警告异常，业务可容忍
     * INFO: 信息异常，正常业务处理
     */
    public enum ErrorLevel {FATAL, ERROR, WARN, INFO}
    
    /**
     * 错误码枚举
     */
    public enum ErrorCode {
        // 系统异常 1000-1999
        SYSTEM_ERROR("1000", "系统异常", ErrorLevel.FATAL),
        DATABASE_ERROR("1001", "数据库异常", ErrorLevel.FATAL),
        REMOTE_CALL_ERROR("1002", "远程调用异常", ErrorLevel.ERROR),
        // 业务异常 2000-2999
        BUSINESS_ERROR("2000", "业务异常", ErrorLevel.ERROR),
        TRANSACTION_NOT_FOUND("2001", "交易不存在", ErrorLevel.WARN),
        INSUFFICIENT_BALANCE("2002", "余额不足", ErrorLevel.WARN),
        TRANSACTION_CONFLICT("2003", "交易冲突", ErrorLevel.ERROR),
        ACCOUNT_STATUS_ERROR("2004", "账户状态异常", ErrorLevel.WARN),
        DUPLICATE_TRANSACTION("2005", "重复交易", ErrorLevel.WARN),
        // 参数校验异常 3000-3999
        VALIDATION_ERROR("3000", "参数校验异常", ErrorLevel.INFO),
        INVALID_AMOUNT("3001", "无效金额", ErrorLevel.INFO),
        INVALID_ACCOUNT("3002", "无效账户", ErrorLevel.INFO),
        // 认证授权异常 4000-4999
        UNAUTHORIZED("4001", "用户未登录", ErrorLevel.WARN),
        FORBIDDEN("4003", "无权限访问", ErrorLevel.ERROR),
        SUCCESS("0000", "操作成功", ErrorLevel.INFO);
        
        private final String code;
        private final String message;
        private final ErrorLevel level;
        ErrorCode(String code, String message, ErrorLevel level) {this.code = code; this.message = message; this.level = level;}
        public String getCode() {return code;}
        public String getMessage() {return message;}
        public ErrorLevel getLevel() {return level;}
    }

    // 分页相关常量
    public static final int DEFAULT_PAGE = 0;        // 默认页码
    public static final int DEFAULT_PAGE_SIZE = 20;  // 默认每页大小
    public static final int MAX_PAGE_SIZE = 100;     // 最大每页大小

    /**
     * 账户状态枚举
     */
    public enum AccountStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        FROZEN("冻结"),
        CLOSED("关闭"),
        SUSPENDED("暂停");
        
        private final String description;
        
        AccountStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 货币枚举
     */
    public enum Currency {
        CNY("人民币", "¥"),
        USD("美元", "$"),
        EUR("欧元", "€"),
        JPY("日元", "¥"),
        GBP("英镑", "£"),
        HKD("港币", "HK$"),
        TWD("台币", "NT$"),
        KRW("韩元", "₩");
        
        private final String name;
        private final String symbol;
        
        Currency(String name, String symbol) {
            this.name = name;
            this.symbol = symbol;
        }
        
        public String getName() {
            return name;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
} 