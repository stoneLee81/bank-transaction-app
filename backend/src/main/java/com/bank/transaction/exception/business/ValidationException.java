package com.bank.transaction.exception.business;

import com.bank.transaction.exception.BaseException;
import com.bank.transaction.util.Constants.ErrorCode;

public class ValidationException extends BaseException {
    public ValidationException(ErrorCode errorCode) {super(errorCode);}

    public ValidationException(ErrorCode errorCode, String detailMessage) {super(errorCode, detailMessage);}

    public ValidationException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
} 