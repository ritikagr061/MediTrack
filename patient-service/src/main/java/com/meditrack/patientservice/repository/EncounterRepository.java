package com.meditrack.patientservice.repository;

import com.meditrack.patientservice.model.Encounter;
import com.meditrack.patientservice.model.EncounterStatus;
import com.meditrack.patientservice.model.EncounterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, UUID> {
    @Query("""
            select e from Encounter e
            where e.hospitalId = :hospitalId
              and (:patientId is null or e.patientId = :patientId)
              and (:appointmentId is null or e.appointmentId = :appointmentId)
              and (:attendingDoctorId is null or e.attendingDoctorId = :attendingDoctorId)
              and (:encounterType is null or e.encounterType = :encounterType)
              and (:status is null or e.status = :status)
              and (:startedFrom is null or e.startedAt >= :startedFrom)
              and (:startedTo is null or e.startedAt <= :startedTo)
            """)
    Page<Encounter> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                     @Param("patientId") UUID patientId,
                                     @Param("appointmentId") UUID appointmentId,
                                     @Param("attendingDoctorId") UUID attendingDoctorId,
                                     @Param("encounterType") EncounterType encounterType,
                                     @Param("status") EncounterStatus status,
                                     @Param("startedFrom") OffsetDateTime startedFrom,
                                     @Param("startedTo") OffsetDateTime startedTo,
                                     Pageable pageable);

    boolean existsByAppointmentId(UUID appointmentId);

    long countByPatientId(UUID patientId);
}
