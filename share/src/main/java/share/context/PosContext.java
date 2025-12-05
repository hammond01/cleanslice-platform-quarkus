package share.context;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Thread-safe context for POS-specific information using ThreadLocal
 * Stores terminal, shift, and location data for audit logging
 */
@ApplicationScoped
public class PosContext {
    
    private static final ThreadLocal<String> TERMINAL_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> STORE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> STORE_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> SHIFT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> EMPLOYEE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> EMPLOYEE_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> DEVICE_INFO = new ThreadLocal<>();
    private static final ThreadLocal<String> PHARMACIST_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> PHARMACIST_NAME = new ThreadLocal<>();
    
    public void setTerminalId(String terminalId) {
        TERMINAL_ID.set(terminalId);
    }
    
    public String getTerminalId() {
        return TERMINAL_ID.get();
    }
    
    public void setStoreId(String storeId) {
        STORE_ID.set(storeId);
    }
    
    public String getStoreId() {
        return STORE_ID.get();
    }
    
    public void setStoreName(String storeName) {
        STORE_NAME.set(storeName);
    }
    
    public String getStoreName() {
        return STORE_NAME.get();
    }
    
    public void setShiftId(String shiftId) {
        SHIFT_ID.set(shiftId);
    }
    
    public String getShiftId() {
        return SHIFT_ID.get();
    }
    
    public void setEmployeeId(String employeeId) {
        EMPLOYEE_ID.set(employeeId);
    }
    
    public String getEmployeeId() {
        return EMPLOYEE_ID.get();
    }
    
    public void setEmployeeName(String employeeName) {
        EMPLOYEE_NAME.set(employeeName);
    }
    
    public String getEmployeeName() {
        return EMPLOYEE_NAME.get();
    }
    
    public void setDeviceInfo(String deviceInfo) {
        DEVICE_INFO.set(deviceInfo);
    }
    
    public String getDeviceInfo() {
        return DEVICE_INFO.get();
    }
    
    public void setPharmacistId(String pharmacistId) {
        PHARMACIST_ID.set(pharmacistId);
    }
    
    public String getPharmacistId() {
        return PHARMACIST_ID.get();
    }
    
    public void setPharmacistName(String pharmacistName) {
        PHARMACIST_NAME.set(pharmacistName);
    }
    
    public String getPharmacistName() {
        return PHARMACIST_NAME.get();
    }
    
    public void clear() {
        TERMINAL_ID.remove();
        STORE_ID.remove();
        STORE_NAME.remove();
        SHIFT_ID.remove();
        EMPLOYEE_ID.remove();
        EMPLOYEE_NAME.remove();
        DEVICE_INFO.remove();
        PHARMACIST_ID.remove();
        PHARMACIST_NAME.remove();
    }
}
