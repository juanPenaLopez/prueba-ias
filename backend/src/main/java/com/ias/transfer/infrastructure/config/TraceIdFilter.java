package com.ias.transfer.infrastructure.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

@Component
public class TraceIdFilter implements WebFilter {

    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        exchange.getAttributes().put(TRACE_ID_KEY, traceId);

        return chain.filter(exchange)
                .contextWrite(Context.of(TRACE_ID_KEY, traceId))
                .doOnEach(signal -> {
                    if (!signal.isOnComplete()) {
                        signal.getContextView().getOrEmpty(TRACE_ID_KEY)
                                .ifPresent(tid -> MDC.put(TRACE_ID_KEY, tid.toString()));
                    }
                })
                .doFinally(signalType -> MDC.remove(TRACE_ID_KEY));
    }
}
