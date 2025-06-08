package com.bank.transaction.service;

import com.bank.transaction.util.Constants.ErrorLevel;
import jakarta.servlet.http.HttpServletRequest;

public interface ErrorLogService {
    // 记录异常到数据库
    void saveError(Exception exception, HttpServletRequest request);
    void saveError(String errorCode, String errorMessage, String detail, ErrorLevel level, HttpServletRequest request);
} 