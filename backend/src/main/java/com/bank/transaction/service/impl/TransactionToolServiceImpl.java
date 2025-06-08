package com.bank.transaction.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionToolService;
import com.bank.transaction.dao.TransactionDao;
import com.bank.transaction.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionToolServiceImpl implements TransactionToolService {
    
    @Autowired
    private TransactionDao transactionDao;
    
    @Autowired
    private AccountService accountService;
    
    /**
     * 按时间排序的交易ID索引（双端队列，新交易添加到头部）
     * 使用 ConcurrentLinkedDeque 保证线程安全和高并发性能
     * 结构：[最新交易ID, 次新交易ID, ..., 最旧交易ID]
     */
    private final ConcurrentLinkedDeque<String> transactionIdsByTime = new ConcurrentLinkedDeque<>();
    
    @Override
    public String generateBankTransactionId() {
        String datePrefix = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TXN" + datePrefix + randomSuffix;
    }
    
    @Override
    public String generateReferenceNumber() {
        String timePrefix = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "REF" + timePrefix + randomSuffix;
    }
    
    @Override
    public String generateIdempotencyKey() {
        return "IDM" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    @Override
    public List<String> getTransactionIdsByTime(int page, int size) {
        List<String> allIds = new ArrayList<>(transactionIdsByTime);
        int start = page * size;
        int end = Math.min(start + size, allIds.size());
        
        if (start >= allIds.size()) {
            return List.of();
        }
        
        return allIds.subList(start, end);
    }
    
    @Override
    public int getTotalTransactionCount() {
        return transactionIdsByTime.size();
    }
    
    @Override
    public void maintainTimeIndex(Transaction transaction) {
        if (transaction == null || transaction.getId() == null) {
            return;
        }
        
        String transactionId = transaction.getId();
        
        // 如果已存在，不重复添加
        if (transactionIdsByTime.contains(transactionId)) {
            return;
        }
        
        // 添加到双端队列的头部（最新的交易在前）
        transactionIdsByTime.addFirst(transactionId);
    }
    
    @Override
    public void removeFromTimeIndex(String transactionId) {
        transactionIdsByTime.remove(transactionId);
    }
    
    /**
     * 银行交易后续处理 - 消息总线异步处理机制
     * 
     * 本方法负责将交易后续处理任务推送到消息总线中心，实现异步解耦处理。
     * 主要特点：
     * 1. 基于 Transaction.id 作为消息路由键，确保消息的唯一性和可追溯性
     * 2. 异步处理，不阻塞主业务流程，提高系统响应性能
     * 3. 通过消息总线实现各个后续处理模块的解耦
     * 4. 支持消息重试机制，确保后续处理的可靠性
     * 
     * 处理流程：
     * - 构建基于 Transaction.id 的消息体
     * - 推送到不同的消息队列进行异步处理
     * - 各个下游系统独立消费和处理
     * 
     * @param transaction 交易对象，包含完整的交易信息
     * @param operationType 操作类型（CREATE/UPDATE/DELETE），用于消息路由和处理逻辑区分
     */
    @Override
    public void performPostTransactionProcessing(Transaction transaction, String operationType) {
        // 空值校验：交易对象不能为空
        if (transaction == null) {
            log.warn("交易对象为空，跳过后续处理 - 操作类型: {}", operationType);
            return;
        }
        
        try {
            log.info("开始推送交易后续处理消息到消息总线 - 交易ID: {}, 操作类型: {}", transaction.getId(), operationType);
            
            // 1. 推送到银行核心系统处理队列（基于Transaction.id异步处理）
            sendToBankCoreSystem(transaction, operationType);
            
            // 2. 推送到审计日志处理队列（基于Transaction.id异步记录）
            recordAuditLog(transaction, operationType);
            
            // 3. 推送到风控检查处理队列（基于Transaction.id异步风控）
            triggerRiskControl(transaction, operationType);
            
            // 4. 推送到通知服务处理队列（基于Transaction.id异步通知）
            sendNotifications(transaction, operationType);
            
            log.info("交易后续处理消息推送完成 - 交易ID: {}", transaction.getId());
        } catch (Exception e) {
            String transactionId = transaction != null ? transaction.getId() : "UNKNOWN";
            log.error("交易后续处理消息推送失败 - 交易ID: {}, 错误: {}", transactionId, e.getMessage(), e);
            // 注意：消息推送失败不抛出异常，避免影响主业务流程
            // TODO: 可考虑实现消息推送失败的补偿机制
        }
    }
    
    /**
     * 推送到银行核心系统处理队列
     * 基于 Transaction.id 构建消息体，异步推送到银行核心系统消息队列
     * 包含：清算系统、账务系统、央行报送等下游处理
     */
    private void sendToBankCoreSystem(Transaction transaction, String operationType) {
        // TODO: 实际实现中推送消息到银行核心系统队列
        // 消息格式: { "transactionId": transaction.getId(), "operationType": operationType, "payload": transaction }
        log.info("推送到银行核心系统队列 - 交易ID: {}, 操作: {}", transaction.getId(), operationType);
        
        // 模拟银行核心系统处理：对于CREATE操作，自动将交易状态更新为COMPLETED
        if ("CREATE".equals(operationType) && transaction.getStatus() == com.bank.transaction.util.Constants.TransactionStatus.PENDING) {
            try {
                // 模拟银行核心系统处理延迟（实际环境中这是异步的）
                Thread.sleep(1); // 1ms模拟处理时间
                
                // 更新账户余额
                updateAccountBalances(transaction);
                
                // 更新交易状态为已完成
                transaction.setStatus(com.bank.transaction.util.Constants.TransactionStatus.COMPLETED);
                transactionDao.save(transaction);
                
                log.info("银行核心系统处理完成 - 交易ID: {}, 状态已更新为: COMPLETED", transaction.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("银行核心系统处理被中断 - 交易ID: {}", transaction.getId());
            } catch (Exception e) {
                log.error("银行核心系统处理失败 - 交易ID: {}, 错误: {}", transaction.getId(), e.getMessage());
                // 如果处理失败，将交易状态设为失败
                transaction.setStatus(com.bank.transaction.util.Constants.TransactionStatus.FAILED);
                transactionDao.save(transaction);
            }
        }
        
        // 示例：messageProducer.send("bank.core.queue", buildMessage(transaction.getId(), operationType, transaction));
    }
    
    /**
     * 更新账户余额
     * 根据交易类型更新相关账户的余额
     */
    private void updateAccountBalances(Transaction transaction) {
        java.math.BigDecimal amount = transaction.getAmount();
        
        switch (transaction.getType()) {
            case DEPOSIT:
                // 存款：增加目标账户余额
                if (transaction.getToAccountId() != null) {
                    accountService.updateBalance(transaction.getToAccountId(), amount);
                    log.info("存款处理完成 - 账户: {}, 增加金额: {}", transaction.getToAccountId(), amount);
                }
                break;
                
            case WITHDRAWAL:
                // 取款：减少源账户余额
                if (transaction.getFromAccountId() != null) {
                    accountService.updateBalance(transaction.getFromAccountId(), amount.negate());
                    log.info("取款处理完成 - 账户: {}, 减少金额: {}", transaction.getFromAccountId(), amount);
                }
                break;
                
            case TRANSFER:
                // 转账：减少源账户余额，增加目标账户余额
                if (transaction.getFromAccountId() != null) {
                    accountService.updateBalance(transaction.getFromAccountId(), amount.negate());
                    log.info("转账扣款完成 - 源账户: {}, 减少金额: {}", transaction.getFromAccountId(), amount);
                }
                if (transaction.getToAccountId() != null) {
                    accountService.updateBalance(transaction.getToAccountId(), amount);
                    log.info("转账入账完成 - 目标账户: {}, 增加金额: {}", transaction.getToAccountId(), amount);
                }
                break;
                
            default:
                log.warn("未处理的交易类型: {} - 交易ID: {}", transaction.getType(), transaction.getId());
                break;
        }
    }
    
    /**
     * 推送到审计日志处理队列
     * 基于 Transaction.id 构建审计消息，异步记录到审计日志系统
     * 确保所有交易操作的完整审计追踪
     */
    private void recordAuditLog(Transaction transaction, String operationType) {
        // TODO: 实际实现中推送消息到审计日志队列
        // 消息格式: { "transactionId": transaction.getId(), "operationType": operationType, "timestamp": now, "auditData": auditInfo }
        log.info("推送到审计日志队列 - 交易ID: {}, 操作: {}, 时间: {}", 
            transaction.getId(), operationType, LocalDateTime.now());
        // 示例：messageProducer.send("audit.log.queue", buildAuditMessage(transaction.getId(), operationType));
    }
    
    /**
     * 推送到风控检查处理队列
     * 基于 Transaction.id 触发异步风控分析，包含反洗钱、异常交易检测等
     * 风控结果通过回调或另一消息队列返回处理
     */
    private void triggerRiskControl(Transaction transaction, String operationType) {
        // TODO: 实际实现中推送消息到风控检查队列
        // 消息格式: { "transactionId": transaction.getId(), "operationType": operationType, "riskData": riskAnalysisData }
        log.info("推送到风控检查队列 - 交易ID: {}, 操作: {}", transaction.getId(), operationType);
        // 示例：messageProducer.send("risk.control.queue", buildRiskMessage(transaction.getId(), operationType, transaction));
    }
    
    /**
     * 推送到通知服务处理队列
     * 基于 Transaction.id 异步发送用户通知，包含微信、短信、邮件等多渠道通知
     * 支持通知模板和个性化推送
     */
    private void sendNotifications(Transaction transaction, String operationType) {
        if ("CREATE".equals(operationType) || "UPDATE".equals(operationType)) {
            // TODO: 实际实现中推送消息到通知服务队列
            // 消息格式: { "transactionId": transaction.getId(), "notificationType": "TRANSACTION_ALERT", "channels": ["SMS", "WECHAT"], "templateData": templateData }
            log.info("推送到通知服务队列 - 交易ID: {}, 类型: {}, 金额: {}", 
                transaction.getId(), transaction.getType(), transaction.getAmount());
            // 示例：messageProducer.send("notification.queue", buildNotificationMessage(transaction.getId(), transaction));
        }
    }
} 