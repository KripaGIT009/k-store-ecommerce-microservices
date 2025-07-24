package com.kstore.notification.scheduler;

import com.kstore.notification.service.NotificationInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationInboxService inboxService;

    /**
     * Clean up expired notifications daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredNotifications() {
        log.info("Starting cleanup of expired notifications");
        
        try {
            int deletedCount = inboxService.cleanupExpiredNotifications();
            
            if (deletedCount > 0) {
                log.info("Cleanup completed. Deleted {} expired notifications", deletedCount);
            } else {
                log.debug("Cleanup completed. No expired notifications found");
            }
            
        } catch (Exception e) {
            log.error("Error during notification cleanup", e);
        }
    }
}
