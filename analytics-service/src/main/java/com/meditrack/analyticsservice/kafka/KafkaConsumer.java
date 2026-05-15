package com.meditrack.analyticsservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @KafkaListener(topics="patient",groupId="analytics")
    public void consumeEvent(ConsumerRecord<String, byte[]> record){
        putTraceIdInMdc(record);
        try{
            byte[] event = record.value();
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info(
                    "Received patient event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", record.topic()),
                    kv("messaging.kafka.partition", record.partition()),
                    kv("messaging.kafka.offset", record.offset()),
                    kv("event.type", patientEvent.getEventType()),
                    kv("patient.id", patientEvent.getPatientId())
            );
        }
        catch (Exception e){
            log.error(
                    "Failed to process patient event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "patient"),
                    e
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
