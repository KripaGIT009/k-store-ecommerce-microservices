package com.kstore.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${notification.firebase.config-file:firebase-service-account.json}")
    private String firebaseConfigFile;

    @Value("${notification.firebase.database-url:}")
    private String firebaseDatabaseUrl;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Check if config file exists in classpath
                ClassPathResource resource = new ClassPathResource(firebaseConfigFile);
                
                if (resource.exists()) {
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                                .setCredentials(GoogleCredentials.fromStream(serviceAccount));

                        if (!firebaseDatabaseUrl.isEmpty()) {
                            optionsBuilder.setDatabaseUrl(firebaseDatabaseUrl);
                        }

                        FirebaseOptions options = optionsBuilder.build();
                        FirebaseApp.initializeApp(options);
                        
                        log.info("Firebase initialized successfully");
                    }
                } else {
                    log.warn("Firebase config file not found: {}. Push notifications will be disabled.", firebaseConfigFile);
                }
            } else {
                log.info("Firebase already initialized");
            }
        } catch (IOException e) {
            log.error("Error initializing Firebase", e);
        }
    }
}
