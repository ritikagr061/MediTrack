package com.meditrack.analyticsservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.event.PatientEvent;

import java.util.Arrays;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics="patient",groupId="analytics")
    public void consumeEvent(byte[] event){
        try{
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Received Patient Event from Kafka Consumer {}",patientEvent.toString());
        }
        catch (Exception e){
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }
}
