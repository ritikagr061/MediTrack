package com.meditrack.billingservice.controller;

import com.meditrack.billingservice.dto.*;
import com.meditrack.billingservice.model.InvoiceStatus;
import com.meditrack.billingservice.model.PaymentStatus;
import com.meditrack.billingservice.model.RefundStatus;
import com.meditrack.billingservice.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<BillingAccountResponseDTO> createBillingAccount(
            @Valid @RequestBody BillingAccountCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createBillingAccount(request));
    }

    @GetMapping("/accounts")
    public ResponseEntity<Page<BillingAccountResponseDTO>> getBillingAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getBillingAccounts(page, size));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<BillingAccountResponseDTO> getBillingAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getBillingAccount(id));
    }

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createInvoice(request));
    }

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
                hospitalId, patientId, appointmentId, encounterId, status, page, size));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getInvoice(id));
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createPayment(request));
    }

    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentResponseDTO>> getPayments(
            @RequestParam(required = false) UUID hospitalId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getPayments(hospitalId, patientId, invoiceId, status, page, size));
    }

    @PostMapping("/refunds")
    public ResponseEntity<RefundResponseDTO> createRefund(@Valid @RequestBody RefundCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(billingService.createRefund(request));
    }

    @GetMapping("/refunds")
    public ResponseEntity<Page<RefundResponseDTO>> getRefunds(
            @RequestParam(required = false) UUID paymentId,
            @RequestParam(required = false) UUID invoiceId,
            @RequestParam(required = false) RefundStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(billingService.getRefunds(paymentId, invoiceId, status, page, size));
    }
}
