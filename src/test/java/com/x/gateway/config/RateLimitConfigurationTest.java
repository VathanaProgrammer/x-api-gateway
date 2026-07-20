package com.x.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitConfigurationTest {

    @Test
    void usesOriginalAddressFromForwardedHeader() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products")
                        .header("X-Forwarded-For", "203.0.113.10, 10.42.0.2"));

        assertThat(RateLimitConfiguration.resolveClientAddress(exchange))
                .isEqualTo("203.0.113.10");
    }

    @Test
    void fallsBackToRemoteAddress() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products")
                        .remoteAddress(new InetSocketAddress("127.0.0.1", 54321)));

        assertThat(RateLimitConfiguration.resolveClientAddress(exchange))
                .isEqualTo("127.0.0.1");
    }
}
