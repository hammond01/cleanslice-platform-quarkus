package io.cleanslice.platform.domain;

import io.cleanslice.platform.domain.enums.LogLevel;

import java.time.LocalDateTime;

public class ErrorLog {

    public Long id;
    public LogLevel level = LogLevel.ERROR;
    public String serviceName;
    public String exceptionType;
    public String message;
    public String stackTrace;
    public String rootCause;
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    public String transactionId;
    public String className;
    public String method;
    public String fileName;
    public Integer lineNumber;
    public String httpMethod;
    public String endpoint;
    public String ipAddress;
    public String userAgent;
    public String metadata;
    public LocalDateTime timestamp = LocalDateTime.now();
    public String errorCode;
    public String category;
    public Boolean resolved = false;
    public String resolution;
    public String terminalId;
    public String storeId;
}



