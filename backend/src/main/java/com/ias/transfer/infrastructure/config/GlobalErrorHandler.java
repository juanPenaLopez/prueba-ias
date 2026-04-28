package com.ias.transfer.infrastructure.config;

import com.ias.transfer.domain.model.BusinessException;
import com.ias.transfer.infrastructure.adapter.in.web.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ApiResponse<?>> handleBusiness(
            BusinessException ex,
            @RequestAttribute("traceId") String traceId
    ) {
        return Mono.just(
                ApiResponse.error(ex.getMessage(), 400, traceId)
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ApiResponse<?>> handleTechnical(
            Exception ex,
            @RequestAttribute("traceId") String traceId
    ) {
        return Mono.just(
                ApiResponse.error("Internal server error", 500, traceId)
        );
    }
}
