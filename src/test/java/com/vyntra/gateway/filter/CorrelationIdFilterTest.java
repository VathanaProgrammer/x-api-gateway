package com.vyntra.gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void preservesValidCorrelationId() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/products")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "request-123")
                .build();
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = exchange -> {
            forwarded.set(exchange);
            return Mono.empty();
        };

        filter.filter(MockServerWebExchange.from(request), chain).block();

        assertEquals("request-123", forwarded.get().getRequest().getHeaders()
                .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }

    @Test
    void replacesInvalidCorrelationId() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/orders")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "invalid value")
                .build();
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        GatewayFilterChain chain = exchange -> {
            forwarded.set(exchange);
            return Mono.empty();
        };

        filter.filter(MockServerWebExchange.from(request), chain).block();

        String correlationId = forwarded.get().getRequest().getHeaders()
                .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertNotNull(correlationId);
        assertEquals(36, correlationId.length());
    }
}
