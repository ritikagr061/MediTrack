package com.meditrack.appointmentservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class NotificationEventProducer {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventProducer.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public NotificationEventProducer(KafkaTemplate<String, byte[]> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(NotificationEvent event) {
        try {
            kafkaTemplate.send(recordWithTraceId(
                    "notifications",
                    event.getEventType(),
                    objectMapper.writeValueAsBytes(event)
            ));
        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to serialize notification event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "notifications"),
                    kv("event.type", event.getEventType()),
                    ex
            );
        } catch (Exception ex) {
            log.error(
                    "Failed to publish notification event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "notifications"),
                    kv("event.type", event.getEventType()),
                    ex
            );
        }
    }

    private ProducerRecord<String, byte[]> recordWithTraceId(String topic, String key, byte[] payload) {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
        String traceId = MDC.get(TRACE_ID_MDC_KEY);
        if (traceId != null && !traceId.isBlank()) {
            record.headers().add(TRACE_ID_HEADER, traceId.getBytes(StandardCharsets.UTF_8));
        }
        return record;
    }
}
