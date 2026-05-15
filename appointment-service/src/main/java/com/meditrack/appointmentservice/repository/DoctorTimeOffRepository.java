package com.meditrack.appointmentservice.repository;

import com.meditrack.appointmentservice.model.DoctorTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorTimeOffRepository extends JpaRepository<DoctorTimeOff, UUID> {
    @Query("""
            select dto from DoctorTimeOff dto
            where dto.hospitalId = :hospitalId
              and dto.doctorId = :doctorId
              and dto.startsAt < :endsAt
              and dto.endsAt > :startsAt
            order by dto.startsAt asc
            """)
    List<DoctorTimeOff> findOverlaps(@Param("hospitalId") UUID hospitalId,
                                     @Param("doctorId") UUID doctorId,
                                     @Param("startsAt") OffsetDateTime startsAt,
                                     @Param("endsAt") OffsetDateTime endsAt);
}
