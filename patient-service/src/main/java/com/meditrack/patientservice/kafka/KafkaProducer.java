package com.meditrack.patientservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meditrack.patientservice.model.Hospital;
import com.meditrack.patientservice.model.Patient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

import java.util.Arrays;

@Slf4j
@Service
public class KafkaProducer {
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
            kafkaTemplate.send("patient",patientEvent.toByteArray());
        }
        catch(Exception e){
            log.error("failed to send event to kafka : {}", patientEvent.toString());
            log.error(Arrays.toString(e.getStackTrace()));
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
            kafkaTemplate.send("hospital", objectMapper.writeValueAsBytes(hospitalSyncEvent));
        } catch (JsonProcessingException e) {
            log.error("failed to serialize hospital event for hospital {}", hospital.getId(), e);
        } catch (Exception e) {
            log.error("failed to send hospital event to kafka for hospital {}", hospital.getId(), e);
        }
    }

}
