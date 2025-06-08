package com.bank.transaction.exception.handler;

import com.bank.transaction.exception.BaseException;
import com.bank.transaction.exception.business.BusinessException;
import com.bank.transaction.exception.business.ValidationException;
import com.bank.transaction.exception.system.SystemException;
import com.bank.transaction.util.Constants.ErrorCode;

public class ServiceExceptionHandler {
    
    public static void handleValidationError(String message) {
        throw new ValidationException(ErrorCode.VALIDATION_ERROR, message);
    }

    public static void handleBusinessError(ErrorCode errorCode, String message) {
        throw new BusinessException(errorCode, message);
    }

    public static void handleSystemError(ErrorCode errorCode, String message) {
        throw new SystemException(errorCode, message);
    }

    public static void handleSystemError(ErrorCode errorCode, Exception cause) {
        throw new SystemException(errorCode, cause);
    }

    public static void logAndThrow(Exception e) {
        System.err.println("Service层异常: " + e.getMessage());
        if (e instanceof BaseException) {
            throw (BaseException) e;
        } else {
            throw new SystemException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }
} 