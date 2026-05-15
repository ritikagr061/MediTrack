package com.meditrack.patientservice.service;

import com.meditrack.patientservice.dto.MedicalProfessionalCreateRequestDTO;
import com.meditrack.patientservice.dto.MedicalProfessionalResponseDTO;
import com.meditrack.patientservice.exception.MedicalProfessionalNotFoundException;
import com.meditrack.patientservice.model.MedicalProfessional;
import com.meditrack.patientservice.model.ProfessionalRoleType;
import com.meditrack.patientservice.repository.MedicalProfessionalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MedicalProfessionalService {
    private final MedicalProfessionalRepository medicalProfessionalRepository;

    public MedicalProfessionalService(MedicalProfessionalRepository medicalProfessionalRepository) {
        this.medicalProfessionalRepository = medicalProfessionalRepository;
    }

    public Page<MedicalProfessionalResponseDTO> getMedicalProfessionals(UUID hospitalId, ProfessionalRoleType roleType,
                                                                        Boolean isActive, String specialty, String search,
                                                                        int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return medicalProfessionalRepository.findAllByFilters(
                        hospitalId,
                        roleType,
                        isActive,
                        normalize(specialty),
                        normalize(search),
                        pageable
                )
                .map(this::toDTO);
    }

    public MedicalProfessionalResponseDTO getMedicalProfessional(UUID id) {
        return toDTO(findByIdOrThrow(id));
    }

    public MedicalProfessionalResponseDTO getMedicalProfessionalForHospital(UUID hospitalId, UUID id) {
        return toDTO(medicalProfessionalRepository.findByIdAndHospitalId(id, hospitalId)
                .orElseThrow(() -> new MedicalProfessionalNotFoundException(
                        "Medical professional with id " + id + " is not found for hospital " + hospitalId)));
    }

    @Transactional
    public MedicalProfessionalResponseDTO createMedicalProfessional(MedicalProfessionalCreateRequestDTO request) {
        MedicalProfessional professional = new MedicalProfessional();
        professional.setHospitalId(request.getHospitalId());
        professional.setUserId(request.getUserId());
        professional.setName(request.getName());
        professional.setRoleType(request.getRoleType());
        professional.setSpecialty(request.getSpecialty());
        professional.setRegistrationNumber(request.getRegistrationNumber());
        professional.setPhone(request.getPhone());
        professional.setEmail(request.getEmail());
        professional.setConsultationFee(request.getConsultationFee());
        professional.setActive(true);
        return toDTO(medicalProfessionalRepository.save(professional));
    }

    private MedicalProfessional findByIdOrThrow(UUID id) {
        return medicalProfessionalRepository.findById(id)
                .orElseThrow(() -> new MedicalProfessionalNotFoundException(
                        "Medical professional with id " + id + " is not found"));
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.trim();
    }

    private MedicalProfessionalResponseDTO toDTO(MedicalProfessional professional) {
        return new MedicalProfessionalResponseDTO(
                professional.getId(),
                professional.getHospitalId(),
                professional.getUserId(),
                professional.getName(),
                professional.getRoleType(),
                professional.getSpecialty(),
                professional.getRegistrationNumber(),
                professional.getPhone(),
                professional.getEmail(),
                professional.getConsultationFee(),
                professional.isActive(),
                professional.getCreatedAt(),
                professional.getUpdatedAt()
        );
    }
}
