package com.meditrack.apigateway.filter;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(TraceIdGatewayFilter.class);
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);

        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        String finalTraceId = traceId;
        ServerHttpRequest requestWithTraceId = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.set(TRACE_ID_HEADER, finalTraceId))
                .build();

        exchange.getResponse().getHeaders().set(TRACE_ID_HEADER, finalTraceId);
        MDC.put(TRACE_ID_MDC_KEY, finalTraceId);

        return chain.filter(exchange.mutate().request(requestWithTraceId).build())
                .doFinally(signalType -> {
                    MDC.put(TRACE_ID_MDC_KEY, finalTraceId);
                    Integer statusCode = exchange.getResponse().getStatusCode() == null
                            ? null
                            : exchange.getResponse().getStatusCode().value();
                    log.info(
                            "HTTP request completed",
                            kv("http.method", exchange.getRequest().getMethod().name()),
                            kv("url.path", exchange.getRequest().getURI().getPath()),
                            kv("http.status_code", statusCode),
                            kv("event.duration_ms", System.currentTimeMillis() - startTime)
                    );
                    MDC.remove(TRACE_ID_MDC_KEY);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
