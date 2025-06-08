package com.bank.transaction.exception.business;

import com.bank.transaction.exception.BaseException;
import com.bank.transaction.util.Constants.ErrorCode;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCode errorCode) {super(errorCode);}

    public BusinessException(ErrorCode errorCode, String detailMessage) {super(errorCode, detailMessage);}

    public BusinessException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
} 