package share.dto;

import share.enums.AuditTypeEnum;
import share.enums.AuditStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for audit events sent via Kafka
 * Enhanced for POS system in pharmaceutical and functional food retail
 * Shared across all services for consistency
 */
public class AuditEvent {
    // Core audit fields
    public AuditTypeEnum auditTypeEnum;
    public String action;
    public String entityType;
    public Integer rowId;
    public Long userId;
    public String username;
    public String serviceName;
    
    // Network & Request context
    public String ipAddress;
    public String userAgent;
    public String httpMethod;
    public String endpoint;
    
    // Change tracking
    public String oldValue;
    public String newValue;
    public String metadata;
    
    // Status tracking
    public AuditStatusEnum status = AuditStatusEnum.SUCCESS;
    public String errorMessage;
    public String stackTrace;
    public String severity;
    
    // Timing
    public LocalDateTime timestamp = LocalDateTime.now();
    public Long durationMs;
    
    // Correlation & Session
    public String correlationId;
    public String sessionId;
    
    // === POS-specific fields ===
    // Terminal & Location
    public String terminalId;
    public String storeId;
    public String storeName;
    public String deviceInfo;
    
    // Transaction context (for sales-related audits)
    public String transactionId;
    public String invoiceNumber;
    public BigDecimal amount;
    public String paymentMethod;
    
    // Inventory context (for product/stock audits)
    public String batchNumber;
    public String lotNumber;
    public LocalDateTime expiryDate;
    public Integer quantityBefore;
    public Integer quantityAfter;
    public Integer quantityChanged;
    
    // Pricing context (for price change audits)
    public BigDecimal priceBefore;
    public BigDecimal priceAfter;
    public String priceChangeReason;
    public String approvedBy;
    
    // Compliance & Regulatory (for pharmaceutical)
    public String prescriptionId;
    public String prescriptionNumber;
    public Boolean requiresPrescription;
    public String pharmacistId;
    public String pharmacistName;
    public String regulatoryNotes;
    
    // Shift & Employee context
    public String shiftId;
    public String employeeId;
    public String employeeName;
    public String role;
}
