package application.dto;

import domain.enums.AuditType;
import domain.enums.Severity;
import share.enums.AuditStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for audit events sent via Kafka
 * Enhanced for POS system in pharmaceutical and functional food retail
 */
public class AuditEvent {
    // Core audit fields
    public AuditType auditType;
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
    public Severity severity;
    
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

    public static class Builder {
        private final AuditEvent event = new AuditEvent();

        public Builder auditType(AuditType auditType) {
            event.auditType = auditType;
            return this;
        }

        public Builder action(String action) {
            event.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            event.entityType = entityType;
            return this;
        }

        public Builder rowId(Integer rowId) {
            event.rowId = rowId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            event.serviceName = serviceName;
            return this;
        }
        
        public Builder userId(Long userId) {
            event.userId = userId;
            return this;
        }
        
        public Builder username(String username) {
            event.username = username;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            event.ipAddress = ipAddress;
            return this;
        }

        public Builder oldValue(String oldValue) {
            event.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            event.newValue = newValue;
            return this;
        }
        
        public Builder metadata(String metadata) {
            event.metadata = metadata;
            return this;
        }

        public Builder status(AuditStatusEnum status) {
            event.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            event.errorMessage = errorMessage;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            event.stackTrace = stackTrace;
            return this;
        }

        public Builder severity(Severity severity) {
            event.severity = severity;
            return this;
        }

        public Builder correlationId(String correlationId) {
            event.correlationId = correlationId;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            event.sessionId = sessionId;
            return this;
        }
        
        public Builder durationMs(Long durationMs) {
            event.durationMs = durationMs;
            return this;
        }
        
        // POS-specific builders
        public Builder terminalId(String terminalId) {
            event.terminalId = terminalId;
            return this;
        }
        
        public Builder storeId(String storeId) {
            event.storeId = storeId;
            return this;
        }
        
        public Builder transactionId(String transactionId) {
            event.transactionId = transactionId;
            return this;
        }
        
        public Builder batchNumber(String batchNumber) {
            event.batchNumber = batchNumber;
            return this;
        }
        
        public Builder priceBefore(BigDecimal priceBefore) {
            event.priceBefore = priceBefore;
            return this;
        }
        
        public Builder priceAfter(BigDecimal priceAfter) {
            event.priceAfter = priceAfter;
            return this;
        }
        
        public Builder prescriptionId(String prescriptionId) {
            event.prescriptionId = prescriptionId;
            return this;
        }
        
        public Builder pharmacistId(String pharmacistId) {
            event.pharmacistId = pharmacistId;
            return this;
        }
        
        public Builder shiftId(String shiftId) {
            event.shiftId = shiftId;
            return this;
        }

        public AuditEvent build() {
            return event;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
