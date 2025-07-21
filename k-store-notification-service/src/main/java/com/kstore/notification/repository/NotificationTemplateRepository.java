package com.kstore.notification.repository;

import com.kstore.notification.entity.Notification;
import com.kstore.notification.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {

    Optional<NotificationTemplate> findByName(String name);

    List<NotificationTemplate> findByTypeAndChannel(Notification.NotificationType type, 
                                                   Notification.NotificationChannel channel);

    List<NotificationTemplate> findByActiveTrue();

    List<NotificationTemplate> findByType(Notification.NotificationType type);

    List<NotificationTemplate> findByChannel(Notification.NotificationChannel channel);

    boolean existsByName(String name);

    List<NotificationTemplate> findByLanguage(String language);

    List<NotificationTemplate> findByTypeAndChannelAndLanguage(Notification.NotificationType type, 
                                                              Notification.NotificationChannel channel, 
                                                              String language);
}
