package com.jobmatrix.jm_notification.repository;

import com.jobmatrix.jm_notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // No additional methods needed for now, JpaRepository provides basic CRUD operations.
}
