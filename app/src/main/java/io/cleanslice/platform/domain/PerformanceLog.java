package io.cleanslice.platform.domain;


import java.time.LocalDateTime;

public class PerformanceLog {

    public Long id;
    public String serviceName;
    public String operation;
    public String operationType;
    public Long durationMs;
    public Long thresholdMs;
    public Boolean isSlow = false;
    public Long memoryUsedMb;
    public Double cpuPercent;
    public Integer threadCount;
    public String sqlQuery;
    public Long queryTimeMs;
    public Integer rowsAffected;
    public Integer connectionPoolSize;
    public String httpMethod;
    public String endpoint;
    public Integer statusCode;
    public String userId;
    public String correlationId;
    public String transactionId;
    public String metadata;
    public LocalDateTime timestamp = LocalDateTime.now();
    public String terminalId;
    public String storeId;
}


