package com.kstore.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNotificationEvent {
    
    private String eventId;
    private String eventType;
    private String source;
    private String templateName;
    private Map<String, String> globalParameters;
    private List<RecipientData> recipients;
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipientData {
        private Long userId;
        private String recipient;
        private Map<String, String> personalizedParameters;
    }
}
