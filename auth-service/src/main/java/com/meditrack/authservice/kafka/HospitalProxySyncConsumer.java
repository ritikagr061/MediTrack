package com.meditrack.authservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.authservice.dto.HospitalSyncEventPayload;
import com.meditrack.authservice.service.HospitalProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class HospitalProxySyncConsumer {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    private final ObjectMapper objectMapper;
    private final HospitalProxyService hospitalProxyService;

    public HospitalProxySyncConsumer(ObjectMapper objectMapper, HospitalProxyService hospitalProxyService) {
        this.objectMapper = objectMapper;
        this.hospitalProxyService = hospitalProxyService;
    }

    @KafkaListener(topics = "hospital", groupId = "${spring.kafka.consumer.group-id:hospital-proxy-sync}")
    public void consume(ConsumerRecord<String, byte[]> record) {
        putTraceIdInMdc(record);
        try {
            byte[] payload = record.value();
            HospitalSyncEventPayload eventPayload = objectMapper.readValue(payload, HospitalSyncEventPayload.class);
            if ("HOSPITAL_UPDATED".equals(eventPayload.getEventType())) {
                hospitalProxyService.upsertFromSyncEvent(eventPayload);
            } else {
                log.info(
                        "Ignoring unsupported hospital event type",
                        kv("messaging.system", "kafka"),
                        kv("messaging.destination.name", record.topic()),
                        kv("event.type", eventPayload.getEventType()),
                        kv("hospital.id", eventPayload.getHospitalId())
                );
            }
        } catch (Exception ex) {
            log.error(
                    "Failed to process hospital sync event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", record.topic()),
                    kv("messaging.kafka.partition", record.partition()),
                    kv("messaging.kafka.offset", record.offset()),
                    ex
            );
        } finally {
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    private void putTraceIdInMdc(ConsumerRecord<String, byte[]> record) {
        Header traceIdHeader = record.headers().lastHeader(TRACE_ID_HEADER);
        if (traceIdHeader != null) {
            MDC.put(TRACE_ID_MDC_KEY, new String(traceIdHeader.value(), StandardCharsets.UTF_8));
        }
    }
}
