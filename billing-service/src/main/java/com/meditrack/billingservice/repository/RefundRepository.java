package com.meditrack.billingservice.repository;

import com.meditrack.billingservice.model.Refund;
import com.meditrack.billingservice.model.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {
    @Query("""
            select r from Refund r
            where (:paymentId is null or r.paymentId = :paymentId)
              and (:invoiceId is null or r.invoiceId = :invoiceId)
              and (:status is null or r.status = :status)
            """)
    Page<Refund> findAllByFilters(@Param("paymentId") UUID paymentId,
                                  @Param("invoiceId") UUID invoiceId,
                                  @Param("status") RefundStatus status,
                                  Pageable pageable);
}
