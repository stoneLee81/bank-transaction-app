package com.bank.transaction.service.impl;

import com.bank.transaction.service.ErrorLogService;
import com.bank.transaction.exception.BaseException;
import com.bank.transaction.util.Constants.ErrorLevel;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {
    private static final Logger log = LoggerFactory.getLogger(ErrorLogServiceImpl.class);

    @Override
    public void saveError(Exception exception, HttpServletRequest request) {
        String errorCode = "1000";
        String errorMessage = "系统异常";
        String detail = exception.getMessage();
        ErrorLevel level = ErrorLevel.ERROR;
        
        if (exception instanceof BaseException) {
            BaseException be = (BaseException) exception;
            errorCode = be.getErrorCode().getCode();
            errorMessage = be.getErrorCode().getMessage();
            detail = be.getDetailMessage() != null ? be.getDetailMessage() : be.getMessage();
            level = be.getErrorCode().getLevel();
        }
        
        saveError(errorCode, errorMessage, detail, level, request);
    }

    @Override
    public void saveError(String errorCode, String errorMessage, String detail, ErrorLevel level, HttpServletRequest request) {
        // 实际项目中这里会插入数据库
        // 目前使用日志记录模拟
        String uri = request != null ? request.getRequestURI() : "unknown";
        String method = request != null ? request.getMethod() : "unknown";
        String clientIp = getClientIp(request);
        
        log.info("错误日志记录 - 时间: {}, 错误级别: {}, 错误码: {}, 错误信息: {}, 详细描述: {}, 请求URI: {}, 请求方法: {}, 客户端IP: {}", 
                LocalDateTime.now(), level, errorCode, errorMessage, detail, uri, method, clientIp);
        
        // 根据异常级别进行不同处理
        if (level == ErrorLevel.FATAL) {
            log.error("*** 致命异常告警 *** - 需要立即通知运维团队");
        } else if (level == ErrorLevel.ERROR) {
            log.warn("*** 重要异常告警 *** - 需要关注处理");
        }
        
        // TODO: 实际项目中替换为数据库插入
        // errorLogRepository.save(new ErrorLog(errorCode, errorMessage, detail, level, request...));
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return "unknown";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 