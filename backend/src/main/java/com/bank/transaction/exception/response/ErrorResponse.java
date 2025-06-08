package com.bank.transaction.exception.response;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code; // 错误码
    private String message; // 错误消息
    private String detail; // 详细描述
    private LocalDateTime timestamp; // 时间戳
    private String path; // 请求路径

    public ErrorResponse() {this.timestamp = LocalDateTime.now();}

    public ErrorResponse(String code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, String detail, String path) {
        this(code, message);
        this.detail = detail;
        this.path = path;
    }

    public String getCode() {return code;}
    public void setCode(String code) {this.code = code;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public String getDetail() {return detail;}
    public void setDetail(String detail) {this.detail = detail;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}
    public String getPath() {return path;}
    public void setPath(String path) {this.path = path;}
} 