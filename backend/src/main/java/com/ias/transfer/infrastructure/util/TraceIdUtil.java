package com.ias.transfer.infrastructure.util;

import reactor.core.publisher.Mono;

public class TraceIdUtil {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String UNKNOWN_TRACE_ID = "UNKNOWN";

    public static Mono<String> getTraceId() {
        return Mono.deferContextual(ctx ->
                Mono.just(ctx.getOrDefault(TRACE_ID_KEY, UNKNOWN_TRACE_ID).toString())
        );
    }
}
