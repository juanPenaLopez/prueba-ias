package com.ias.transfer.infrastructure.adapter.in.web.dto;

public record ApiResponse<T>(
        T data,
        int status,
        String message,
        String traceId
) {
    public static <T> ApiResponse<T> ok(T data, String traceId) {
        return new ApiResponse<>(data, 200, "Respuesta ok", traceId);
    }

    public static ApiResponse<?> error(String message, int status, String traceId) {
        return new ApiResponse<>(null, status, message, traceId);
    }
}
