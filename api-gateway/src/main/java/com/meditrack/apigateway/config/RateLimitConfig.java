package com.meditrack.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.security.Principal;

@Configuration
public class RateLimitConfig {

    private static final String AUTH_PATH_PREFIX = "/api/auth";

    @Bean
    KeyResolver rateLimitKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getURI().getPath();

            if (path.equals(AUTH_PATH_PREFIX) || path.startsWith(AUTH_PATH_PREFIX + "/")) {
                return Mono.just("ip:" + clientIp(exchange));
            }

            return exchange.getPrincipal()
                    .map(Principal::getName)
                    .filter(name -> !name.isBlank())
                    .map(name -> "user:" + name)
                    .switchIfEmpty(Mono.just("ip:" + clientIp(exchange)));
        };
    }

    private String clientIp(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }
        return remoteAddress.getAddress().getHostAddress();
    }
}
