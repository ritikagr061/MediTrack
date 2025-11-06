package com.meditrack.patientservice.kafka;

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
    @Autowired
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
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

}
