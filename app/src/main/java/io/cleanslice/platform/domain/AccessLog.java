package io.cleanslice.platform.domain;


import java.time.LocalDateTime;

public class AccessLog {

    public Long id;
    public String serviceName;
    public String httpMethod;
    public String endpoint;
    public String path;
    public String queryString;
    public String requestId;
    public String ipAddress;
    public String userAgent;
    public String referer;
    public String origin;
    public Integer requestSize;
    public Integer statusCode;
    public Integer responseSize;
    public String contentType;
    public Long responseTimeMs;
    public LocalDateTime requestTime;
    public LocalDateTime responseTime;
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    public String authMethod;
    public Boolean authenticated = false;
    public String metadata;
    public LocalDateTime timestamp = LocalDateTime.now();
    public String terminalId;
    public String storeId;
}


