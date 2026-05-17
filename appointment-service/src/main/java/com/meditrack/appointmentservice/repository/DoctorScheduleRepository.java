package com.meditrack.appointmentservice.repository;

import com.meditrack.appointmentservice.model.DoctorSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    @Query("""
            select ds from DoctorSchedule ds
            where ds.hospitalId = :hospitalId
              and ds.doctorId = :doctorId
              and ds.isActive = true
            order by ds.dayOfWeek asc, ds.startTime asc
            """)
    List<DoctorSchedule> findActiveSchedules(@Param("hospitalId") UUID hospitalId,
                                             @Param("doctorId") UUID doctorId);

    @Query("""
            select ds from DoctorSchedule ds
            where ds.hospitalId = :hospitalId
              and ds.doctorId = :doctorId
              and ds.dayOfWeek = :dayOfWeek
              and ds.isActive = true
            order by ds.startTime asc
            """)
    List<DoctorSchedule> findActiveSchedulesForDay(@Param("hospitalId") UUID hospitalId,
                                                   @Param("doctorId") UUID doctorId,
                                                   @Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("""
            select ds from DoctorSchedule ds
            where ds.hospitalId = :hospitalId
              and ds.doctorId = :doctorId
              and ds.dayOfWeek = :dayOfWeek
              and ds.isActive = true
              and ds.startTime <= :startTime
              and ds.endTime >= :endTime
            order by ds.startTime asc
            """)
    List<DoctorSchedule> findActiveSchedulesContainingSlotForBooking(@Param("hospitalId") UUID hospitalId,
                                                                     @Param("doctorId") UUID doctorId,
                                                                     @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                                     @Param("startTime") LocalTime startTime,
                                                                     @Param("endTime") LocalTime endTime);
}
