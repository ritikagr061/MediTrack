package com.meditrack.billingservice.controller;

import com.meditrack.billingservice.dto.*;
import com.meditrack.billingservice.model.InvoiceStatus;
import com.meditrack.billingservice.model.PaymentStatus;
import com.meditrack.billingservice.model.RefundStatus;
import com.meditrack.billingservice.security.AuthContext;
import com.meditrack.billingservice.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/billing")
public class BillingController {
    private final BillingService billingService;
    private final AuthContext authContext;

    public BillingController(BillingService billingService, AuthContext authContext) {
        this.billingService = billingService;
        this.authContext = authContext;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @PostMapping("/accounts")
    public ResponseEntity<BillingAccountResponseDTO> createBillingAccount(
            @Valid @RequestBody BillingAccountCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createBillingAccount(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @GetMapping("/accounts")
    public ResponseEntity<Page<BillingAccountResponseDTO>> getBillingAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getBillingAccounts(authContext.hospitalId(), page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @GetMapping("/accounts/{id}")
    public ResponseEntity<BillingAccountResponseDTO> getBillingAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getBillingAccount(id, authContext.hospitalId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createInvoice(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION','DOCTOR')")
    @GetMapping("/invoices")
    public ResponseEntity<Page<InvoiceResponseDTO>> getInvoices(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID appointmentId,
            @RequestParam(required = false) UUID encounterId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getInvoices(
                authContext.scopedHospitalId(hospitalId), patientId, appointmentId, encounterId, status, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION','DOCTOR')")
    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getInvoice(id, authContext.hospitalId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @PostMapping("/payments")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentCreateRequestDTO request) {
        request.setHospitalId(authContext.scopedHospitalId(request.getHospitalId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createPayment(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentResponseDTO>> getPayments(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getPayments(authContext.scopedHospitalId(hospitalId), patientId, invoiceId, status, page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @PostMapping("/refunds")
    public ResponseEntity<RefundResponseDTO> createRefund(@Valid @RequestBody RefundCreateRequestDTO request) {
        billingService.assertPaymentBelongsToHospital(request.getPaymentId(), authContext.hospitalId());
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createRefund(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF','RECEPTION')")
    @GetMapping("/refunds")
    public ResponseEntity<Page<RefundResponseDTO>> getRefunds(
            @RequestParam(required = false) UUID paymentId,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) RefundStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getRefunds(authContext.hospitalId(), paymentId, invoiceId, status, page, size));
    }
}
