package com.meditrack.billingservice.service;

import com.meditrack.billingservice.dto.*;
import com.meditrack.billingservice.exception.BillingEntityNotFoundException;
import com.meditrack.billingservice.exception.InvalidBillingRequestException;
import com.meditrack.billingservice.kafka.NotificationEvent;
import com.meditrack.billingservice.kafka.NotificationEventProducer;
import com.meditrack.billingservice.model.*;
import com.meditrack.billingservice.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class BillingService {
    private final BillingAccountRepository billingAccountRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final NotificationEventProducer notificationEventProducer;

    public BillingService(BillingAccountRepository billingAccountRepository,
                          InvoiceRepository invoiceRepository,
                          InvoiceItemRepository invoiceItemRepository,
                          PaymentRepository paymentRepository,
                          RefundRepository refundRepository,
                          NotificationEventProducer notificationEventProducer) {
        this.billingAccountRepository = billingAccountRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceItemRepository = invoiceItemRepository;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.notificationEventProducer = notificationEventProducer;
    }

    @Transactional
    public BillingAccountResponseDTO createBillingAccount(BillingAccountCreateRequestDTO request) {
        BillingAccount account = billingAccountRepository.findByPatientId(request.getPatientId()).orElseGet(BillingAccount::new);
        account.setHospitalId(request.getHospitalId());
        account.setPatientId(request.getPatientId());
        account.setPatientName(request.getPatientName());
        account.setPatientEmail(request.getPatientEmail());
        return toDTO(billingAccountRepository.save(account));
    }

    public BillingAccountResponseDTO getBillingAccount(UUID id) {
        return toDTO(findBillingAccountOrThrow(id));
    }

    public Page<BillingAccountResponseDTO> getBillingAccounts(int page, int size) {
        return billingAccountRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDTO);
    }

    @Transactional
    public InvoiceResponseDTO createInvoice(InvoiceCreateRequestDTO request) {
        BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getUnitAmount().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = defaultAmount(request.getDiscountAmount());
        BigDecimal tax = defaultAmount(request.getTaxAmount());
        BigDecimal total = subtotal.subtract(discount).add(tax);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBillingRequestException("Invoice total cannot be negative");
        }

        Invoice invoice = new Invoice();
        invoice.setHospitalId(request.getHospitalId());
        invoice.setPatientId(request.getPatientId());
        invoice.setAppointmentId(request.getAppointmentId());
        invoice.setEncounterId(request.getEncounterId());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setSubtotalAmount(subtotal);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setDueAmount(total);
        invoice.setDueAt(request.getDueAt());
        Invoice saved = invoiceRepository.save(invoice);

        for (InvoiceItemRequestDTO itemRequest : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoiceId(saved.getId());
            item.setItemName(itemRequest.getItemName());
            item.setItemType(itemRequest.getItemType());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitAmount(itemRequest.getUnitAmount());
            item.setTotalAmount(itemRequest.getUnitAmount().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            invoiceItemRepository.save(item);
        }

        publishBillingNotification("INVOICE_CREATED", saved, "Invoice generated",
                "An invoice " + saved.getInvoiceNumber() + " for " + saved.getTotalAmount() + " has been generated.");
        return getInvoice(saved.getId());
    }

    public Page<InvoiceResponseDTO> getInvoices(UUID hospitalId, UUID patientId, UUID appointmentId, UUID encounterId,
                                                InvoiceStatus status, int page, int size) {
        return invoiceRepository.findAllByFilters(hospitalId, patientId, appointmentId, encounterId, status,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issuedAt")))
                .map(invoice -> toDTO(invoice, invoiceItemRepository.findByInvoiceId(invoice.getId())));
    }

    public InvoiceResponseDTO getInvoice(UUID id) {
        Invoice invoice = findInvoiceOrThrow(id);
        return toDTO(invoice, invoiceItemRepository.findByInvoiceId(id));
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentCreateRequestDTO request) {
        Payment payment = new Payment();
        payment.setHospitalId(request.getHospitalId());
        payment.setPatientId(request.getPatientId());
        payment.setInvoiceId(request.getInvoiceId());
        payment.setAppointmentId(request.getAppointmentId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(request.getStatus() == null ? PaymentStatus.SUCCESS : request.getStatus());
        payment.setTransactionReference(request.getTransactionReference());
        payment.setFailureReason(request.getFailureReason());
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            payment.setPaidAt(OffsetDateTime.now(ZoneOffset.UTC));
            applySuccessfulPayment(payment);
        }
        Payment saved = paymentRepository.save(payment);
        publishPaymentNotification(saved);
        return toDTO(saved);
    }

    public Page<PaymentResponseDTO> getPayments(UUID hospitalId, UUID patientId, UUID invoiceId,
                                                PaymentStatus status, int page, int size) {
        return paymentRepository.findAllByFilters(hospitalId, patientId, invoiceId, status,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDTO);
    }

    @Transactional
    public RefundResponseDTO createRefund(RefundCreateRequestDTO request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new BillingEntityNotFoundException("Payment with id " + request.getPaymentId() + " is not found"));
        Refund refund = new Refund();
        refund.setPaymentId(request.getPaymentId());
        refund.setInvoiceId(request.getInvoiceId() == null ? payment.getInvoiceId() : request.getInvoiceId());
        refund.setAppointmentId(request.getAppointmentId() == null ? payment.getAppointmentId() : request.getAppointmentId());
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(request.getStatus() == null ? RefundStatus.SUCCESS : request.getStatus());
        if (refund.getStatus() == RefundStatus.SUCCESS) {
            refund.setProcessedAt(OffsetDateTime.now(ZoneOffset.UTC));
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }
        Refund saved = refundRepository.save(refund);
        publishRefundNotification(saved, payment);
        return toDTO(saved);
    }

    public Page<RefundResponseDTO> getRefunds(UUID paymentId, UUID invoiceId, RefundStatus status, int page, int size) {
        return refundRepository.findAllByFilters(paymentId, invoiceId, status,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDTO);
    }

    private void applySuccessfulPayment(Payment payment) {
        if (payment.getInvoiceId() == null) {
            return;
        }
        Invoice invoice = findInvoiceOrThrow(payment.getInvoiceId());
        BigDecimal due = invoice.getDueAmount().subtract(payment.getAmount()).max(BigDecimal.ZERO);
        invoice.setDueAmount(due);
        invoice.setStatus(due.compareTo(BigDecimal.ZERO) == 0 ? InvoiceStatus.PAID : InvoiceStatus.PARTIALLY_PAID);
        invoiceRepository.save(invoice);
    }

    private void publishBillingNotification(String eventType, Invoice invoice, String subject, String body) {
        BillingAccount account = billingAccountRepository.findByPatientId(invoice.getPatientId()).orElse(null);
        NotificationEvent event = baseNotification(eventType, invoice.getHospitalId(), invoice.getPatientId(),
                account, invoice.getId(), subject, body);
        notificationEventProducer.publish(event);
    }

    private void publishPaymentNotification(Payment payment) {
        BillingAccount account = billingAccountRepository.findByPatientId(payment.getPatientId()).orElse(null);
        String eventType = payment.getStatus() == PaymentStatus.SUCCESS ? "PAYMENT_SUCCESSFUL" : "PAYMENT_FAILED";
        String subject = payment.getStatus() == PaymentStatus.SUCCESS ? "Payment received" : "Payment failed";
        String body = payment.getStatus() == PaymentStatus.SUCCESS
                ? "Your payment of " + payment.getAmount() + " was received."
                : "Your payment of " + payment.getAmount() + " failed. " + nullToEmpty(payment.getFailureReason());
        notificationEventProducer.publish(baseNotification(eventType, payment.getHospitalId(), payment.getPatientId(),
                account, payment.getId(), subject, body));
    }

    private void publishRefundNotification(Refund refund, Payment payment) {
        BillingAccount account = billingAccountRepository.findByPatientId(payment.getPatientId()).orElse(null);
        String eventType = refund.getStatus() == RefundStatus.SUCCESS ? "REFUND_PROCESSED" : "REFUND_FAILED";
        String subject = refund.getStatus() == RefundStatus.SUCCESS ? "Refund processed" : "Refund failed";
        String body = "Refund of " + refund.getAmount() + " is " + refund.getStatus() + ".";
        notificationEventProducer.publish(baseNotification(eventType, payment.getHospitalId(), payment.getPatientId(),
                account, refund.getId(), subject, body));
    }

    private NotificationEvent baseNotification(String eventType, UUID hospitalId, UUID patientId,
                                               BillingAccount account, UUID sourceEntityId, String subject, String body) {
        NotificationEvent event = new NotificationEvent();
        event.setEventType(eventType);
        event.setHospitalId(hospitalId);
        event.setPatientId(patientId);
        event.setRecipientName(account == null ? null : account.getPatientName());
        event.setRecipientEmail(account == null ? null : account.getPatientEmail());
        event.setChannel("EMAIL");
        event.setTemplateCode(eventType);
        event.setSubject(subject);
        event.setBody(body);
        event.setSourceService("BILLING_SERVICE");
        event.setSourceEntityId(sourceEntityId);
        return event;
    }

    private BillingAccount findBillingAccountOrThrow(UUID id) {
        return billingAccountRepository.findById(id)
                .orElseThrow(() -> new BillingEntityNotFoundException("Billing account with id " + id + " is not found"));
    }

    private Invoice findInvoiceOrThrow(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new BillingEntityNotFoundException("Invoice with id " + id + " is not found"));
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private BillingAccountResponseDTO toDTO(BillingAccount account) {
        return new BillingAccountResponseDTO(account.getId(), account.getHospitalId(), account.getPatientId(),
                account.getPatientName(), account.getPatientEmail(), account.getAccountCode(),
                account.getBalanceAmount(), account.getStatus(), account.getCreatedAt(), account.getUpdatedAt());
    }

    private InvoiceResponseDTO toDTO(Invoice invoice, List<InvoiceItem> items) {
        return new InvoiceResponseDTO(invoice.getId(), invoice.getHospitalId(), invoice.getPatientId(),
                invoice.getAppointmentId(), invoice.getEncounterId(), invoice.getInvoiceNumber(),
                invoice.getInvoiceType(), invoice.getStatus(), invoice.getSubtotalAmount(),
                invoice.getDiscountAmount(), invoice.getTaxAmount(), invoice.getTotalAmount(),
                invoice.getDueAmount(), invoice.getIssuedAt(), invoice.getDueAt(),
                items.stream().map(this::toDTO).toList());
    }

    private InvoiceItemResponseDTO toDTO(InvoiceItem item) {
        return new InvoiceItemResponseDTO(item.getId(), item.getItemName(), item.getItemType(),
                item.getQuantity(), item.getUnitAmount(), item.getTotalAmount());
    }

    private PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(payment.getId(), payment.getHospitalId(), payment.getPatientId(),
                payment.getInvoiceId(), payment.getAppointmentId(), payment.getAmount(),
                payment.getPaymentMethod(), payment.getStatus(), payment.getTransactionReference(),
                payment.getPaidAt(), payment.getFailureReason());
    }

    private RefundResponseDTO toDTO(Refund refund) {
        return new RefundResponseDTO(refund.getId(), refund.getPaymentId(), refund.getInvoiceId(),
                refund.getAppointmentId(), refund.getAmount(), refund.getReason(), refund.getStatus(),
                refund.getProcessedAt());
    }
}
