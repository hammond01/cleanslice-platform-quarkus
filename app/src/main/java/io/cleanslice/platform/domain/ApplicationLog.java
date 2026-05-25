package io.cleanslice.platform.domain;

import io.cleanslice.platform.domain.enums.LogLevel;

import java.time.LocalDateTime;

public class ApplicationLog {

    public Long id;
    public LogLevel level;
    public String serviceName;
    public String logger;
    public String message;
    public String thread;
    public String method;
    public String className;
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    public String transactionId;
    public String fileName;
    public Integer lineNumber;
    public String metadata;
    public LocalDateTime timestamp = LocalDateTime.now();
    public String terminalId;
    public String storeId;
    public String shiftId;
}



