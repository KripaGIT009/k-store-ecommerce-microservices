package com.kstore.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNotificationRequest {

    private String templateName;
    private Map<String, String> globalParameters;
    private java.util.List<RecipientData> recipients;

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
