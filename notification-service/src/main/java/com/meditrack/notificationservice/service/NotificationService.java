package com.meditrack.notificationservice.service;

import com.meditrack.notificationservice.dto.NotificationCreateRequestDTO;
import com.meditrack.notificationservice.dto.NotificationEventDTO;
import com.meditrack.notificationservice.dto.NotificationResponseDTO;
import com.meditrack.notificationservice.exception.InvalidNotificationException;
import com.meditrack.notificationservice.exception.NotificationNotFoundException;
import com.meditrack.notificationservice.model.DeliveryAttemptStatus;
import com.meditrack.notificationservice.model.NotificationChannel;
import com.meditrack.notificationservice.model.NotificationDeliveryAttempt;
import com.meditrack.notificationservice.model.NotificationRequest;
import com.meditrack.notificationservice.model.NotificationStatus;
import com.meditrack.notificationservice.repository.NotificationDeliveryAttemptRepository;
import com.meditrack.notificationservice.repository.NotificationRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRequestRepository notificationRequestRepository;
    private final NotificationDeliveryAttemptRepository deliveryAttemptRepository;

    public NotificationService(NotificationRequestRepository notificationRequestRepository,
                               NotificationDeliveryAttemptRepository deliveryAttemptRepository) {
        this.notificationRequestRepository = notificationRequestRepository;
        this.deliveryAttemptRepository = deliveryAttemptRepository;
    }

    @Transactional
    public NotificationResponseDTO createNotification(NotificationCreateRequestDTO request) {
        NotificationRequest notification = new NotificationRequest();
        notification.setHospitalId(request.getHospitalId());
        notification.setRecipientUserId(request.getRecipientUserId());
        notification.setPatientId(request.getPatientId());
        notification.setChannel(request.getChannel());
        notification.setRecipientAddress(request.getRecipientAddress());
        notification.setRecipientName(request.getRecipientName());
        notification.setTemplateCode(request.getTemplateCode());
        notification.setSubject(request.getSubject());
        notification.setBody(request.getBody());
        notification.setScheduledAt(request.getScheduledAt());
        notification.setSourceService("MANUAL");
        notification.setSourceEventType("MANUAL_NOTIFICATION");
        return toDTO(saveAndDeliver(notification));
    }

    @Transactional
    public NotificationResponseDTO createNotificationFromEvent(NotificationEventDTO event) {
        NotificationChannel channel = event.getChannel() == null ? NotificationChannel.EMAIL : event.getChannel();
        String recipientAddress = resolveRecipientAddress(event, channel);

        NotificationRequest notification = new NotificationRequest();
        notification.setHospitalId(event.getHospitalId());
        notification.setRecipientUserId(event.getRecipientUserId());
        notification.setPatientId(event.getPatientId());
        notification.setChannel(channel);
        notification.setRecipientAddress(recipientAddress);
        notification.setRecipientName(event.getRecipientName());
        notification.setTemplateCode(event.getTemplateCode());
        notification.setSubject(event.getSubject());
        notification.setBody(event.getBody());
        notification.setScheduledAt(event.getScheduledAt());
        notification.setSourceService(event.getSourceService() == null ? "UNKNOWN" : event.getSourceService());
        notification.setSourceEventType(event.getEventType());
        notification.setSourceEntityId(event.getSourceEntityId());
        return toDTO(saveAndDeliver(notification));
    }

    public Page<NotificationResponseDTO> getNotifications(UUID hospitalId, UUID patientId, NotificationStatus status,
                                                          int page, int size) {
        return notificationRequestRepository.findAllByFilters(
                        hospitalId,
                        patientId,
                        status,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(this::toDTO);
    }

    public NotificationResponseDTO getNotification(UUID id) {
        return toDTO(notificationRequestRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification request with id " + id + " is not found")));
    }

    private NotificationRequest saveAndDeliver(NotificationRequest notification) {
        if (notification.getHospitalId() == null) {
            throw new InvalidNotificationException("hospitalId is required");
        }
        if (notification.getRecipientAddress() == null || notification.getRecipientAddress().isBlank()) {
            throw new InvalidNotificationException("recipientAddress is required");
        }
        if (notification.getBody() == null || notification.getBody().isBlank()) {
            throw new InvalidNotificationException("body is required");
        }

        NotificationRequest saved = notificationRequestRepository.save(notification);
        simulateDelivery(saved);
        return notificationRequestRepository.save(saved);
    }

    private void simulateDelivery(NotificationRequest notification) {
        long attemptCount = deliveryAttemptRepository.countByNotificationRequestId(notification.getId());

        NotificationDeliveryAttempt attempt = new NotificationDeliveryAttempt();
        attempt.setNotificationRequestId(notification.getId());
        attempt.setAttemptNumber((int) attemptCount + 1);
        attempt.setProviderName("SIMULATED");
        attempt.setProviderMessageId("SIM-" + UUID.randomUUID());
        attempt.setStatus(DeliveryAttemptStatus.SUCCESS);
        attempt.setResponseMessage("Notification delivery simulated successfully");
        deliveryAttemptRepository.save(attempt);

        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(OffsetDateTime.now(ZoneOffset.UTC));
        log.info(
                "Notification delivery simulated",
                kv("notification.id", notification.getId()),
                kv("notification.channel", notification.getChannel()),
                kv("notification.status", notification.getStatus()),
                kv("event.type", notification.getSourceEventType()),
                kv("delivery.provider.name", attempt.getProviderName()),
                kv("delivery.attempt_number", attempt.getAttemptNumber())
        );
    }

    private String resolveRecipientAddress(NotificationEventDTO event, NotificationChannel channel) {
        if (channel == NotificationChannel.EMAIL) {
            return event.getRecipientEmail();
        }
        if (channel == NotificationChannel.SMS || channel == NotificationChannel.WHATSAPP) {
            return event.getRecipientPhone();
        }
        return event.getRecipientEmail();
    }

    private NotificationResponseDTO toDTO(NotificationRequest notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getHospitalId(),
                notification.getRecipientUserId(),
                notification.getPatientId(),
                notification.getChannel(),
                notification.getRecipientAddress(),
                notification.getRecipientName(),
                notification.getTemplateCode(),
                notification.getSubject(),
                notification.getBody(),
                notification.getStatus(),
                notification.getScheduledAt(),
                notification.getSentAt(),
                notification.getFailureReason(),
                notification.getSourceService(),
                notification.getSourceEventType(),
                notification.getSourceEntityId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
