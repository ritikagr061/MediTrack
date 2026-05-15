package com.meditrack.notificationservice.repository;

import com.meditrack.notificationservice.model.NotificationDeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationDeliveryAttemptRepository extends JpaRepository<NotificationDeliveryAttempt, UUID> {
    long countByNotificationRequestId(UUID notificationRequestId);
}
