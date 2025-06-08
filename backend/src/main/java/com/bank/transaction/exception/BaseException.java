package com.bank.transaction.exception;

import com.bank.transaction.util.Constants.ErrorCode;

public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detailMessage;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    public BaseException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage() + ": " + detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    public ErrorCode getErrorCode() {return errorCode;}
    public String getDetailMessage() {return detailMessage;}
} 