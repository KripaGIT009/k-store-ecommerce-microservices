package com.kstore.notification.service;

import java.util.Map;

public interface TemplateService {
    
    /**
     * Process a template by replacing placeholders with actual values
     * @param template The template string with placeholders in {{variable}} format
     * @param parameters Map of variable names to their values
     * @return Processed template with variables replaced
     */
    String processTemplate(String template, Map<String, String> parameters);
    
    /**
     * Validate if a template has correct syntax
     * @param template The template to validate
     * @return true if template is valid, false otherwise
     */
    boolean validateTemplate(String template);
}
