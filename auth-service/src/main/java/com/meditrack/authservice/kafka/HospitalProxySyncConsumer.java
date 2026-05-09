package com.meditrack.authservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.authservice.dto.HospitalSyncEventPayload;
import com.meditrack.authservice.service.HospitalProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HospitalProxySyncConsumer {
    private final ObjectMapper objectMapper;
    private final HospitalProxyService hospitalProxyService;

    public HospitalProxySyncConsumer(ObjectMapper objectMapper, HospitalProxyService hospitalProxyService) {
        this.objectMapper = objectMapper;
        this.hospitalProxyService = hospitalProxyService;
    }

    @KafkaListener(topics = "hospital", groupId = "${spring.kafka.consumer.group-id:hospital-proxy-sync}")
    public void consume(byte[] payload) {
        try {
            HospitalSyncEventPayload eventPayload = objectMapper.readValue(payload, HospitalSyncEventPayload.class);
            if ("HOSPITAL_UPDATED".equals(eventPayload.getEventType())) {
                hospitalProxyService.upsertFromSyncEvent(eventPayload);
            } else {
                log.info("Ignoring unsupported hospital event type {}", eventPayload.getEventType());
            }
        } catch (Exception ex) {
            log.error("Failed to process hospital sync event", ex);
        }
    }
}
