package share;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    public boolean success;
    public T data;
    public ErrorData error;
    public String requestId;
    public LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, ErrorData error, String requestId) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.requestId = requestId;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> ok(T data, String requestId) {
        return new ApiResponse<>(true, data, null, requestId);
    }

    public static <T> ApiResponse<T> fail(String code, String message, String requestId) {
        return new ApiResponse<>(false, null, new ErrorData(code, message), requestId);
    }

    public static class ErrorData {
        public String code;
        public String message;

        public ErrorData(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}