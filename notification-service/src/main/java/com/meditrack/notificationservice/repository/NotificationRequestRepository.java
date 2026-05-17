package com.meditrack.notificationservice.repository;

import com.meditrack.notificationservice.model.NotificationRequest;
import com.meditrack.notificationservice.model.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface NotificationRequestRepository extends JpaRepository<NotificationRequest, UUID> {
    @Query("""
            select nr from NotificationRequest nr
            where (:hospitalId is null or nr.hospitalId = :hospitalId)
              and (:patientId is null or nr.patientId = :patientId)
              and (:status is null or nr.status = :status)
            """)
    Page<NotificationRequest> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                               @Param("patientId") UUID patientId,
                                               @Param("status") NotificationStatus status,
                                               Pageable pageable);

    Optional<NotificationRequest> findByIdAndHospitalId(UUID id, UUID hospitalId);
}
