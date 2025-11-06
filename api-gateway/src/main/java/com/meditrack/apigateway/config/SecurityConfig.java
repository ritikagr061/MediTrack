package com.meditrack.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/auth/**", "/actuator/health").permitAll()
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((swe, e) -> jsonError(swe, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"))
                        .accessDeniedHandler((swe, e) -> jsonError(swe, HttpStatus.FORBIDDEN, "FORBIDDEN"))
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    private Mono<Void> jsonError(ServerWebExchange swe, HttpStatus status, String code) {
        var resp = swe.getResponse();
        resp.setStatusCode(status);
        var buffer = resp.bufferFactory().wrap(("{\"error\":\"" + code + "\"}").getBytes());
        resp.getHeaders().add("Content-Type", "application/json");
        return resp.writeWith(Mono.just(buffer));
    }
}
