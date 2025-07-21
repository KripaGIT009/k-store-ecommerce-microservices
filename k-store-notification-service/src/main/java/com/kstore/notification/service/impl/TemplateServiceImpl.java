package com.kstore.notification.service.impl;

import com.kstore.notification.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    @Override
    public String processTemplate(String template, Map<String, String> parameters) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (parameters == null || parameters.isEmpty()) {
            return template;
        }

        String processedTemplate = template;
        
        try {
            Matcher matcher = VARIABLE_PATTERN.matcher(template);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                String replacement = parameters.getOrDefault(variableName, matcher.group(0));
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(result);

            processedTemplate = result.toString();
            
            log.debug("Template processing completed. Variables replaced: {}", 
                     parameters.keySet().size());
            
        } catch (Exception e) {
            log.error("Error processing template", e);
            return template; // Return original template if processing fails
        }

        return processedTemplate;
    }

    @Override
    public boolean validateTemplate(String template) {
        if (template == null || template.isEmpty()) {
            return false;
        }

        try {
            Matcher matcher = VARIABLE_PATTERN.matcher(template);
            
            // Check if all variable patterns are properly formed
            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                if (variableName.isEmpty()) {
                    log.warn("Empty variable name found in template");
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error validating template", e);
            return false;
        }
    }
}
