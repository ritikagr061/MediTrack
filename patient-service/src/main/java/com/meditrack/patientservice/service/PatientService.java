package com.meditrack.patientservice.service;

import com.meditrack.patientservice.dto.PatientCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseCreateRequestDTO;
import com.meditrack.patientservice.dto.PatientDiseaseResponseDTO;
import com.meditrack.patientservice.dto.PatientDiseaseUpdateRequestDTO;
import com.meditrack.patientservice.dto.PatientDuplicateCheckResponseDTO;
import com.meditrack.patientservice.dto.PatientResponseDTO;
import com.meditrack.patientservice.dto.PatientStatusUpdateRequestDTO;
import com.meditrack.patientservice.dto.PatientSummaryResponseDTO;
import com.meditrack.patientservice.dto.PatientUpdateRequestDTO;
import com.meditrack.patientservice.exception.DiseaseNotFoundException;
import com.meditrack.patientservice.exception.EmailAlreadyExistsException;
import com.meditrack.patientservice.exception.PatientNotFoundException;
import com.meditrack.patientservice.grpc.BillingServiceGrpcClient;
import com.meditrack.patientservice.kafka.KafkaProducer;
import com.meditrack.patientservice.mapper.PatientMapper;
import com.meditrack.patientservice.model.Patient;
import com.meditrack.patientservice.model.PatientDisease;
import com.meditrack.patientservice.repository.PatientDiseaseRepository;
import com.meditrack.patientservice.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final PatientDiseaseRepository patientDiseaseRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          PatientDiseaseRepository patientDiseaseRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.patientDiseaseRepository = patientDiseaseRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public Page<PatientResponseDTO> getPatients(String search, UUID hospitalId, Boolean isActive, int page, int size,
                                                String sortBy, Sort.Direction sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        String normalizedSearch = normalizeSearch(search);
        return patientRepository.findAllByFilters(normalizedSearch, hospitalId, isActive, pageable)
                .map(PatientMapper::toDTO);
    }

    public PatientResponseDTO getPatientById(UUID id) {
        return PatientMapper.toDTO(findPatientOrThrow(id));
    }

    @Transactional
    public PatientResponseDTO savePatient(PatientCreateRequestDTO request) {
        validateUniqueEmail(request.getHospitalId(), request.getEmail(), null);
        Patient patient = patientRepository.save(PatientMapper.toModel(request));
        createBillingAccountSafely(patient);
        publishPatientCreatedEventSafely(patient);
        return PatientMapper.toDTO(patient);
    }

    @Transactional
    public PatientResponseDTO updatePatient(PatientUpdateRequestDTO request, UUID id) {
        Patient patient = findPatientOrThrow(id);

        if (request.getEmail() != null) {
            validateUniqueEmail(patient.getHospitalId(), request.getEmail(), id);
            patient.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            patient.setAddress(request.getAddress());
        }
        if (request.getName() != null) {
            patient.setName(request.getName());
        }
        if (request.getPhone() != null) {
            patient.setPhone(request.getPhone());
        }
        if (request.getAadhar() != null) {
            patient.setAadhar(request.getAadhar());
        }
        if (request.getPan() != null) {
            patient.setPan(request.getPan());
        }
        if (request.getDateOfBirth() != null) {
            patient.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            patient.setGender(request.getGender());
        }
        if (request.getIsActive() != null) {
            patient.setActive(request.getIsActive());
        }

        return PatientMapper.toDTO(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponseDTO updatePatientStatus(UUID id, PatientStatusUpdateRequestDTO request) {
        Patient patient = findPatientOrThrow(id);
        patient.setActive(request.getIsActive());
        return PatientMapper.toDTO(patientRepository.save(patient));
    }

    public PatientSummaryResponseDTO getPatientSummary(UUID id) {
        Patient patient = findPatientOrThrow(id);
        long diseaseCount = patientDiseaseRepository.countByPatientId(id);
        return PatientMapper.toSummaryDTO(patient, diseaseCount);
    }

    public PatientDuplicateCheckResponseDTO checkDuplicate(UUID hospitalId, String email, String phone,
                                                           String aadhar, String pan, UUID excludePatientId) {
        List<PatientResponseDTO> matches = patientRepository.findPotentialDuplicates(
                        hospitalId,
                        normalizeValue(email),
                        normalizeValue(phone),
                        normalizeValue(aadhar),
                        normalizeValue(pan),
                        excludePatientId
                ).stream()
                .map(PatientMapper::toDTO)
                .toList();

        return new PatientDuplicateCheckResponseDTO(!matches.isEmpty(), matches.size(), matches);
    }

    @Transactional
    public PatientDiseaseResponseDTO addDisease(UUID patientId, PatientDiseaseCreateRequestDTO request) {
        Patient patient = findPatientOrThrow(patientId);
        PatientDisease patientDisease = patientDiseaseRepository.save(PatientMapper.toDiseaseModel(patient, request));
        return PatientMapper.toDiseaseDTO(patientDisease);
    }

    public List<PatientDiseaseResponseDTO> getDiseases(UUID patientId) {
        findPatientOrThrow(patientId);
        return patientDiseaseRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(PatientMapper::toDiseaseDTO)
                .toList();
    }

    @Transactional
    public PatientDiseaseResponseDTO updateDisease(UUID patientId, UUID diseaseId, PatientDiseaseUpdateRequestDTO request) {
        findPatientOrThrow(patientId);
        PatientDisease patientDisease = patientDiseaseRepository.findByIdAndPatientId(diseaseId, patientId)
                .orElseThrow(() -> new DiseaseNotFoundException(
                        "Disease with id " + diseaseId + " is not found for patient " + patientId));

        if (request.getDiseaseName() != null) {
            patientDisease.setDiseaseName(request.getDiseaseName());
        }
        if (request.getDiseaseCode() != null) {
            patientDisease.setDiseaseCode(request.getDiseaseCode());
        }
        if (request.getIsChronic() != null) {
            patientDisease.setChronic(request.getIsChronic());
        }
        if (request.getDiagnosedAt() != null) {
            patientDisease.setDiagnosedAt(request.getDiagnosedAt());
        }
        if (request.getNotes() != null) {
            patientDisease.setNotes(request.getNotes());
        }

        return PatientMapper.toDiseaseDTO(patientDiseaseRepository.save(patientDisease));
    }

    @Transactional
    public void deleteDisease(UUID patientId, UUID diseaseId) {
        findPatientOrThrow(patientId);
        PatientDisease patientDisease = patientDiseaseRepository.findByIdAndPatientId(diseaseId, patientId)
                .orElseThrow(() -> new DiseaseNotFoundException(
                        "Disease with id " + diseaseId + " is not found for patient " + patientId));
        patientDiseaseRepository.delete(patientDisease);
    }

    private Patient findPatientOrThrow(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient with the id " + id + " is not found"));
    }

    private void validateUniqueEmail(UUID hospitalId, String email, UUID currentPatientId) {
        boolean exists = currentPatientId == null
                ? patientRepository.existsByHospitalIdAndEmailIgnoreCase(hospitalId, email)
                : patientRepository.existsByHospitalIdAndEmailIgnoreCaseAndIdNot(hospitalId, email, currentPatientId);

        if (exists) {
            throw new EmailAlreadyExistsException(
                    "The email " + email + " already exists for hospital " + hospitalId);
        }
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return search.trim();
    }

    private String normalizeValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private void createBillingAccountSafely(Patient patient) {
        try {
            billingServiceGrpcClient.createBillingAccount(patient.getId().toString(), patient.getName(), patient.getEmail());
        } catch (Exception ex) {
            log.warn("Billing account creation failed for patient {}: {}", patient.getId(), ex.getMessage());
        }
    }

    private void publishPatientCreatedEventSafely(Patient patient) {
        try {
            kafkaProducer.createEvent(patient);
        } catch (Exception ex) {
            log.warn("Kafka event publish failed for patient {}: {}", patient.getId(), ex.getMessage());
        }
    }
}
