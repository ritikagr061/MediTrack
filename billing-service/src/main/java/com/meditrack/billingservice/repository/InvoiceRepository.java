package com.meditrack.billingservice.repository;

import com.meditrack.billingservice.model.Invoice;
import com.meditrack.billingservice.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query("""
            select i from Invoice i
            where (:hospitalId is null or i.hospitalId = :hospitalId)
              and (:patientId is null or i.patientId = :patientId)
              and (:appointmentId is null or i.appointmentId = :appointmentId)
              and (:encounterId is null or i.encounterId = :encounterId)
              and (:status is null or i.status = :status)
            """)
    Page<Invoice> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                   @Param("patientId") UUID patientId,
                                   @Param("appointmentId") UUID appointmentId,
                                   @Param("encounterId") UUID encounterId,
                                   @Param("status") InvoiceStatus status,
                                   Pageable pageable);
}
