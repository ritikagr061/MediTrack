package com.meditrack.patientservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.patientservice.model.Hospital;
import com.meditrack.patientservice.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

import java.nio.charset.StandardCharsets;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Slf4j
@Service
public class KafkaProducer {
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    private KafkaTemplate<String,byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Autowired
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void createEvent(Patient patient){

        PatientEvent patientEvent = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setEmail(patient.getEmail())
                .setName(patient.getName())
                .setEventType("PATIENT_CREATED")
                .build();

        try{
            kafkaTemplate.send(recordWithTraceId("patient", patient.getId().toString(), patientEvent.toByteArray()));
        }
        catch(Exception e){
            log.error(
                    "Failed to publish patient event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "patient"),
                    kv("event.type", patientEvent.getEventType()),
                    kv("patient.id", patient.getId()),
                    e
            );
        }
    }

    public void publishHospitalUpsertEvent(Hospital hospital) {
        HospitalSyncEvent hospitalSyncEvent = new HospitalSyncEvent();
        hospitalSyncEvent.setEventType("HOSPITAL_UPDATED");
        hospitalSyncEvent.setHospitalId(hospital.getId());
        hospitalSyncEvent.setHospitalCode(hospital.getCode());
        hospitalSyncEvent.setHospitalName(hospital.getName());
        hospitalSyncEvent.setLogoUrl(hospital.getLogoUrl());
        hospitalSyncEvent.setLoginWelcomeText(hospital.getLoginWelcomeText());
        hospitalSyncEvent.setPrimaryColor(hospital.getPrimaryColor());
        hospitalSyncEvent.setSecondaryColor(hospital.getSecondaryColor());
        hospitalSyncEvent.setActive(hospital.isActive());
        hospitalSyncEvent.setOccurredAt(hospital.getUpdatedAt());

        try {
            kafkaTemplate.send(recordWithTraceId(
                    "hospital",
                    hospital.getId().toString(),
                    objectMapper.writeValueAsBytes(hospitalSyncEvent)
            ));
        } catch (JsonProcessingException e) {
            log.error(
                    "Failed to serialize hospital event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "hospital"),
                    kv("event.type", hospitalSyncEvent.getEventType()),
                    kv("hospital.id", hospital.getId()),
                    e
            );
        } catch (Exception e) {
            log.error(
                    "Failed to publish hospital event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "hospital"),
                    kv("event.type", hospitalSyncEvent.getEventType()),
                    kv("hospital.id", hospital.getId()),
                    e
            );
        }
    }

    public void publishNotificationEvent(NotificationEvent notificationEvent) {
        try {
            kafkaTemplate.send(recordWithTraceId(
                    "notifications",
                    notificationEvent.getEventType(),
                    objectMapper.writeValueAsBytes(notificationEvent)
            ));
        } catch (JsonProcessingException e) {
            log.error(
                    "Failed to serialize notification event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "notifications"),
                    kv("event.type", notificationEvent.getEventType()),
                    e
            );
        } catch (Exception e) {
            log.error(
                    "Failed to publish notification event",
                    kv("messaging.system", "kafka"),
                    kv("messaging.destination.name", "notifications"),
                    kv("event.type", notificationEvent.getEventType()),
                    e
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
