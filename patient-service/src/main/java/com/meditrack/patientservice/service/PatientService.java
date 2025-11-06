package com.meditrack.patientservice.service;

import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.dto.PatientUpdateRequestDTO;
import com.meditrack.patientservice.exception.EmailAlreadyExistsException;
import com.meditrack.patientservice.exception.PatientNotFoundException;
import com.meditrack.patientservice.grpc.BillingServiceGrpcClient;
import com.meditrack.patientservice.kafka.KafkaProducer;
import com.meditrack.patientservice.mapper.PatientMapper;
import com.meditrack.patientservice.model.Patient;
import com.meditrack.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatient(){
        List<Patient> patients= patientRepository.findAll();
        List<PatientResponseDTO> patientResponseDTOs = patients.stream()
                .map((patient)-> PatientMapper.toDTO(patient)).collect(Collectors.toList());
        return patientResponseDTOs;
    }

    public PatientResponseDTO savePatient(PatientCreateRequestDTO request){
        boolean alreadyExists=patientRepository.existsByEmail(request.getEmail());
        if(alreadyExists){
            throw new EmailAlreadyExistsException("The email "+request.getEmail()+" already exists");
        }
        Patient patient=patientRepository.save(PatientMapper.toModel(request));
        billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(), patient.getEmail());
        kafkaProducer.createEvent(patient);
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(PatientUpdateRequestDTO request, UUID id){
        Optional<Patient> patient = patientRepository.findById(id);
        if(!patient.isPresent()){
            throw new PatientNotFoundException("Patient with the id "+id+" is not found");
        }
        if(request.getEmail()!=null){
            boolean emailExists= patientRepository.existsByEmailAndIdNot(request.getEmail(),id);
            if(emailExists){
                throw new EmailAlreadyExistsException("The email "+request.getEmail()+" already exists");
            }
            patient.get().setEmail(request.getEmail());
        }
        if(request.getAddress() != null)
            patient.get().setAddress(request.getAddress());
        if(request.getName() != null)
            patient.get().setName(request.getName());
        if(request.getDateOfBirth()!=null)
            patient.get().setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        if(request.getRegisteredDate()!=null)
            patient.get().setRegisteredDate(LocalDate.parse(request.getRegisteredDate()));

        Patient finalPatient=patientRepository.save(patient.get());
        return PatientMapper.toDTO(finalPatient);
    }

    public PatientResponseDTO deletePatient(UUID id){
        Optional<Patient> patient = patientRepository.findById(id);
        if (!patient.isPresent()) {
            throw new PatientNotFoundException("Patient with the id "+id+" is not found");
        }
        patientRepository.deleteById(id);
        return PatientMapper.toDTO(patient.get());
    }
}
