package com.meditrack.patientservice.repository;

import com.meditrack.patientservice.model.PatientDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientDiseaseRepository extends JpaRepository<PatientDisease, UUID> {
    List<PatientDisease> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    Optional<PatientDisease> findByIdAndPatientId(UUID id, UUID patientId);

    long countByPatientId(UUID patientId);
}
