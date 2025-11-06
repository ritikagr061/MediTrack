package com.meditrack.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // test profile disables JwtDecoderConfig
class ApiGatewayApplicationTests {
  @Test void contextLoads() {}
}
