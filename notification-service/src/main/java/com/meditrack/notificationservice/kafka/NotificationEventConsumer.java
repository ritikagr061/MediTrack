package com.meditrack.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.notificationservice.dto.NotificationEventDTO;
import com.meditrack.notificationservice.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class NotificationEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public NotificationEventConsumer(ObjectMapper objectMapper, NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "notifications", groupId = "notification-service")
    public void consume(ConsumerRecord<String, byte[]> record) {
        putTraceIdInMdc(record);
        try {
            byte[] payload = record.value();
            NotificationEventDTO event = objectMapper.readValue(payload, NotificationEventDTO.class);
            log.info(
                    "Received notification event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", record.topic()),
                    kv("messaging.kafka.partition", record.partition()),
                    kv("messaging.kafka.offset", record.offset()),
                    kv("event.type", event.getEventType())
            );
            notificationService.createNotificationFromEvent(event);
        } catch (Exception ex) {
            log.error(
                    "Failed to process notification event",
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
