package com.meditrack.appointmentservice.config;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Bean
    RestClient patientServiceRestClient(@Value("${patient.service.url}") String patientServiceUrl) {
        return RestClient.builder()
                .baseUrl(patientServiceUrl)
                .requestInterceptor((request, body, execution) -> {
                    String traceId = MDC.get(TRACE_ID_MDC_KEY);
                    if (traceId != null && !traceId.isBlank()) {
                        request.getHeaders().set(TRACE_ID_HEADER, traceId);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
