package com.bank.transaction.exception.system;

import com.bank.transaction.exception.BaseException;
import com.bank.transaction.util.Constants.ErrorCode;

public class SystemException extends BaseException {
    public SystemException(ErrorCode errorCode) {super(errorCode);}

    public SystemException(ErrorCode errorCode, String detailMessage) {super(errorCode, detailMessage);}

    public SystemException(ErrorCode errorCode, Throwable cause) {super(errorCode, cause);}
} 