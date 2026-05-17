package com.meditrack.patientservice.service;

import com.meditrack.patientservice.dto.HospitalCreateRequestDTO;
import com.meditrack.patientservice.dto.HospitalLoginConfigResponseDTO;
import com.meditrack.patientservice.dto.HospitalResponseDTO;
import com.meditrack.patientservice.dto.HospitalUpdateRequestDTO;
import com.meditrack.patientservice.exception.HospitalCodeAlreadyExistsException;
import com.meditrack.patientservice.exception.HospitalNotFoundException;
import com.meditrack.patientservice.kafka.KafkaProducer;
import com.meditrack.patientservice.mapper.PatientMapper;
import com.meditrack.patientservice.model.Hospital;
import com.meditrack.patientservice.repository.HospitalRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final KafkaProducer kafkaProducer;

    public HospitalService(HospitalRepository hospitalRepository, KafkaProducer kafkaProducer) {
        this.hospitalRepository = hospitalRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<HospitalResponseDTO> getHospitals(Boolean isActive) {
        return hospitalRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .filter(hospital -> isActive == null || hospital.isActive() == isActive)
                .map(PatientMapper::toHospitalDTO)
                .toList();
    }

    @Cacheable(value = "patient-service:hospitals", key = "'id:' + #id")
    public HospitalResponseDTO getHospitalById(UUID id) {
        return PatientMapper.toHospitalDTO(findHospitalOrThrow(id));
    }

    @Cacheable(value = "patient-service:hospitals", key = "'code:' + #code.toLowerCase()")
    public HospitalResponseDTO getHospitalByCode(String code) {
        return PatientMapper.toHospitalDTO(findHospitalByCodeOrThrow(code));
    }

    @Cacheable(value = "patient-service:hospital-login-configs", key = "#code.toLowerCase()")
    public HospitalLoginConfigResponseDTO getHospitalLoginConfigByCode(String code) {
        return PatientMapper.toHospitalLoginConfigDTO(findHospitalByCodeOrThrow(code));
    }

    @Transactional
    @CacheEvict(value = {"patient-service:hospitals", "patient-service:hospital-login-configs"}, allEntries = true)
    public HospitalResponseDTO createHospital(HospitalCreateRequestDTO request) {
        validateCodeUniqueness(request.getCode(), null);
        Hospital hospital = hospitalRepository.save(PatientMapper.toHospitalModel(request));
        kafkaProducer.publishHospitalUpsertEvent(hospital);
        return PatientMapper.toHospitalDTO(hospital);
    }

    @Transactional
    @CacheEvict(value = {"patient-service:hospitals", "patient-service:hospital-login-configs"}, allEntries = true)
    public HospitalResponseDTO updateHospital(UUID id, HospitalUpdateRequestDTO request) {
        Hospital hospital = findHospitalOrThrow(id);

        if (request.getCode() != null) {
            validateCodeUniqueness(request.getCode(), id);
            hospital.setCode(request.getCode().trim());
        }
        if (request.getName() != null) {
            hospital.setName(request.getName());
        }
        if (request.getAddress() != null) {
            hospital.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            hospital.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            hospital.setEmail(request.getEmail());
        }
        if (request.getLogoUrl() != null) {
            hospital.setLogoUrl(request.getLogoUrl());
        }
        if (request.getPrimaryColor() != null) {
            hospital.setPrimaryColor(request.getPrimaryColor());
        }
        if (request.getSecondaryColor() != null) {
            hospital.setSecondaryColor(request.getSecondaryColor());
        }
        if (request.getLoginWelcomeText() != null) {
            hospital.setLoginWelcomeText(request.getLoginWelcomeText());
        }
        if (request.getIsActive() != null) {
            hospital.setActive(request.getIsActive());
        }

        Hospital updatedHospital = hospitalRepository.save(hospital);
        kafkaProducer.publishHospitalUpsertEvent(updatedHospital);
        return PatientMapper.toHospitalDTO(updatedHospital);
    }

    private Hospital findHospitalOrThrow(UUID id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new HospitalNotFoundException("Hospital with id " + id + " is not found"));
    }

    private Hospital findHospitalByCodeOrThrow(String code) {
        return hospitalRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new HospitalNotFoundException("Hospital with code " + code + " is not found"));
    }

    private void validateCodeUniqueness(String code, UUID currentHospitalId) {
        boolean exists = currentHospitalId == null
                ? hospitalRepository.existsByCodeIgnoreCase(code)
                : hospitalRepository.existsByCodeIgnoreCaseAndIdNot(code, currentHospitalId);

        if (exists) {
            throw new HospitalCodeAlreadyExistsException("Hospital code " + code + " already exists");
        }
    }
}
