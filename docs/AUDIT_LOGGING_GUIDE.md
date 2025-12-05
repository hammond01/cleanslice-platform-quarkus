# Audit Logging System for Pharmaceutical POS

## Overview
The audit logging system has been comprehensively upgraded to meet strict compliance requirements for pharmaceutical and functional food retail industries.

## Key Enhancements

### 1. **AuditEvent DTO - Complete Structure**
Extended with critical fields for POS operations:

#### Core Fields (Required)
- `auditType`, `action`, `entityType`, `rowId`
- `serviceName`, `username`, `timestamp`, `correlationId`

#### POS-Specific Fields
- **Terminal & Location**: `terminalId`, `storeId`, `storeName`, `deviceInfo`
- **Transaction**: `transactionId`, `invoiceNumber`, `amount`, `paymentMethod`
- **Inventory**: `batchNumber`, `lotNumber`, `expiryDate`, `quantityBefore/After/Changed`
- **Pricing**: `priceBefore`, `priceAfter`, `priceChangeReason`, `approvedBy`
- **Pharmaceutical Compliance**: `prescriptionId`, `requiresPrescription`, `pharmacistId`, `pharmacistName`, `regulatoryNotes`
- **Shift Management**: `shiftId`, `employeeId`, `employeeName`, `role`

### 2. **PosContext - Thread-safe Context**
```java
@Inject PosContext posContext;

posContext.setTerminalId("POS-001");
posContext.setStoreId("STORE-HCM-01");
posContext.setShiftId("SHIFT-20231205-MORNING");
posContext.setPharmacistId("PHARM-123");
```

### 3. **AuditHelper - Utility Service**
Automatically populates context and handles errors:

```java
@Inject AuditHelper auditHelper;

AuditEvent event = auditHelper.createBaseEvent("product-service", AuditTypeEnum.CRUD, "CREATE")
    .entityType("Product")
    .rowId(product.RowId)
    .metadata("Created product: " + product.name)
    .priceBefore(BigDecimal.ZERO)
    .priceAfter(product.price)
    .build();

auditHelper.publishCrudEvent(event);
```

### 4. **KafkaAuditEventPublisherAdapter - Enhanced**

#### Validation
- Validates all required fields
- Warnings for missing important fields
- Skips publish if event is invalid

#### Error Handling
- Async acknowledgment with callbacks
- Detailed logging for success/failure
- Non-blocking - doesn't throw exceptions to avoid disrupting business flow

#### Kafka Partitioning
- Priority: `correlationId` → `rowId` → `transactionId` → timestamp
- Ensures ordering for same entity

#### LocalDateTime Serialization
- JavaTimeModule configured
- ISO-8601 format
- Timezone aware

## Use Cases for Pharmaceutical Retail

### 1. Audit Price Changes
```java
AuditEvent event = AuditEvent.builder()
    .auditType(AuditTypeEnum.CRUD)
    .action("PRICE_CHANGE")
    .entityType("Product")
    .rowId(productId)
    .priceBefore(oldPrice)
    .priceAfter(newPrice)
    .priceChangeReason("Promotion 20%")
    .approvedBy("MANAGER-001")
    .build();
```

### 2. Audit Prescription Validation
```java
AuditEvent event = AuditEvent.builder()
    .auditType(AuditTypeEnum.COMPLIANCE)
    .action("PRESCRIPTION_VALIDATED")
    .prescriptionId("RX-2023-12345")
    .prescriptionNumber("RX123456789")
    .requiresPrescription(true)
    .pharmacistId("PHARM-001")
    .pharmacistName("Pharmacist Nguyen Van A")
    .regulatoryNotes("Verified prescription valid until 2024-01-15")
    .build();
```

### 3. Audit Inventory Changes
```java
AuditEvent event = AuditEvent.builder()
    .auditType(AuditTypeEnum.CRUD)
    .action("INVENTORY_ADJUST")
    .batchNumber("BATCH-2023-001")
    .lotNumber("LOT-12345")
    .expiryDate(LocalDateTime.of(2025, 12, 31, 0, 0))
    .quantityBefore(100)
    .quantityAfter(95)
    .quantityChanged(-5)
    .metadata("Sold 5 units")
    .build();
```

### 4. Audit Transaction Complete
```java
AuditEvent event = AuditEvent.builder()
    .auditType(AuditTypeEnum.TRANSACTION)
    .action("SALE_COMPLETED")
    .transactionId("TXN-2023-12-05-001")
    .invoiceNumber("INV-001234")
    .amount(new BigDecimal("250000"))
    .paymentMethod("CASH")
    .terminalId("POS-001")
    .shiftId("SHIFT-MORNING")
    .build();
```

## Compliance & Regulatory

### Complete Traceability
- **Who**: userId, username, employeeId, pharmacistId
- **What**: action, entityType, oldValue, newValue
- **When**: timestamp, shiftId
- **Where**: storeId, storeName, terminalId
- **Why**: metadata, priceChangeReason, regulatoryNotes
- **How**: httpMethod, endpoint, deviceInfo

### Tamper-proof
- CorrelationId cho tracing
- Timestamp không thể thay đổi
- Kafka retention policy
- Immutable audit log

### Pharmaceutical Specific
- Prescription tracking
- Pharmacist validation
- Batch/Lot traceability
- Expiry date monitoring

## Best Practices

### 1. Always set PosContext on login
```java
posContext.setTerminalId(request.terminalId);
posContext.setStoreId(request.storeId);
posContext.setShiftId(shiftService.getCurrentShift());
posContext.setEmployeeId(currentUser.id);
```

### 2. Use AuditHelper instead of manual creation
```java
// Good ✓
auditHelper.createBaseEvent("service", type, action)...

// Avoid ✗
new AuditEvent(); event.timestamp = ...; event.userId = ...
```

### 3. Add detailed metadata
```java
.metadata(String.format("Updated price from %s to %s, reason: %s", 
    oldPrice, newPrice, reason))
```

### 4. Don't ignore errors
```java
try {
    auditHelper.publishCrudEvent(event);
} catch (Exception ex) {
    Log.error("Audit failed - CRITICAL", ex);
    // Consider alternative logging
}
```

## Monitoring

### Log Levels
- **DEBUG**: Successful audit publish với full details
- **INFO**: Error audit events
- **WARN**: Missing optional fields, validation warnings
- **ERROR**: Failed to publish, validation failures

### Metrics to Monitor
- Audit event publish rate
- Failed publish count
- Validation failure rate
- Average event size
- Kafka lag

## Migration Notes

### Breaking Changes
- `entityId` (String) → `rowId` (Integer)
- Removed old Builder methods

### Backward Compatible
- Default values cho new fields
- Optional POS context
- Graceful degradation nếu thiếu fields

## Files Changed
1. ✅ `AuditEvent.java` - Enhanced DTO
2. ✅ `KafkaAuditEventPublisherAdapter.java` - Validation & error handling
3. ✅ `PosContext.java` - NEW - Thread-safe context
4. ✅ `AuditHelper.java` - NEW - Utility service
5. ✅ Fixed entityId → rowId bugs

## Next Steps
1. Implement audit interceptor cho REST endpoints
2. Add audit dashboard
3. Setup Kafka retention policy (recommend 90+ days for pharma)
4. Add audit query API
5. Implement audit export cho compliance reports
