package com.meditrack.appointmentservice.repository;

import com.meditrack.appointmentservice.model.Appointment;
import com.meditrack.appointmentservice.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("""
            select a from Appointment a
            where a.hospitalId = :hospitalId
              and (:patientId is null or a.patientId = :patientId)
              and (:doctorId is null or a.doctorId = :doctorId)
              and (:status is null or a.status = :status)
            order by a.startsAt desc
            """)
    Page<Appointment> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                       @Param("patientId") UUID patientId,
                                       @Param("doctorId") UUID doctorId,
                                       @Param("status") AppointmentStatus status,
                                       Pageable pageable);

    @Query("""
            select count(a) > 0 from Appointment a
            where a.hospitalId = :hospitalId
              and a.doctorId = :doctorId
              and a.status in :statuses
              and a.startsAt < :endsAt
              and a.endsAt > :startsAt
            """)
    boolean existsDoctorOverlap(@Param("hospitalId") UUID hospitalId,
                                @Param("doctorId") UUID doctorId,
                                @Param("startsAt") OffsetDateTime startsAt,
                                @Param("endsAt") OffsetDateTime endsAt,
                                @Param("statuses") Collection<AppointmentStatus> statuses);

    @Query("""
            select count(a) > 0 from Appointment a
            where a.hospitalId = :hospitalId
              and a.patientId = :patientId
              and a.status in :statuses
              and a.startsAt < :endsAt
              and a.endsAt > :startsAt
            """)
    boolean existsPatientOverlap(@Param("hospitalId") UUID hospitalId,
                                 @Param("patientId") UUID patientId,
                                 @Param("startsAt") OffsetDateTime startsAt,
                                 @Param("endsAt") OffsetDateTime endsAt,
                                 @Param("statuses") Collection<AppointmentStatus> statuses);

    @Query("""
            select a from Appointment a
            where a.hospitalId = :hospitalId
              and a.doctorId = :doctorId
              and a.status in :statuses
              and a.startsAt < :endsAt
              and a.endsAt > :startsAt
            """)
    java.util.List<Appointment> findDoctorOverlaps(@Param("hospitalId") UUID hospitalId,
                                                   @Param("doctorId") UUID doctorId,
                                                   @Param("startsAt") OffsetDateTime startsAt,
                                                   @Param("endsAt") OffsetDateTime endsAt,
                                                   @Param("statuses") Collection<AppointmentStatus> statuses);

    Optional<Appointment> findByIdAndHospitalId(UUID id, UUID hospitalId);
}
