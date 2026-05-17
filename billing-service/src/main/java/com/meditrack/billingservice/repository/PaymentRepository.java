package com.meditrack.billingservice.repository;

import com.meditrack.billingservice.model.Payment;
import com.meditrack.billingservice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query("""
            select p from Payment p
            where (:hospitalId is null or p.hospitalId = :hospitalId)
              and (:patientId is null or p.patientId = :patientId)
              and (:invoiceId is null or p.invoiceId = :invoiceId)
              and (:status is null or p.status = :status)
            """)
    Page<Payment> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                   @Param("patientId") UUID patientId,
                                   @Param("invoiceId") UUID invoiceId,
                                   @Param("status") PaymentStatus status,
                                   Pageable pageable);

    Optional<Payment> findByIdAndHospitalId(UUID id, UUID hospitalId);
}
