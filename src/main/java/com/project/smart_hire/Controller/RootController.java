package com.project.smart_hire.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private DataSource dataSource;

    @Value("${spring.application.name:SmartHire}")
    private String appName;

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "SmartHire Backend API is running successfully!");
        response.put("application", appName);
        response.put("timestamp", LocalDateTime.now());
        response.put("server", "Render Web Service");
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Check database connection
        Map<String, Object> db = new HashMap<>();
        try {
            if (dataSource != null) {
                Connection connection = dataSource.getConnection();
                db.put("status", "UP");
                db.put("database", connection.getMetaData().getDatabaseProductName());
                connection.close();
            } else {
                db.put("status", "DOWN");
                db.put("error", "DataSource not initialized");
            }
        } catch (Exception e) {
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
        }
        health.put("database", db);
        
        return health;
    }

    @GetMapping("/debug/env")
    public Map<String, Object> debugEnv() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("app.name", appName);
        debug.put("db.host", environment.getProperty("DB_HOST"));
        debug.put("db.port", environment.getProperty("DB_PORT"));
        debug.put("db.name", environment.getProperty("DB_NAME"));
        debug.put("db.username", environment.getProperty("DB_USERNAME"));
        debug.put("server.port", environment.getProperty("server.port"));
        debug.put("gemini.api.key.set", environment.getProperty("app.gemini.api-key") != null);
        debug.put("timestamp", LocalDateTime.now());
        return debug;
    }
}
