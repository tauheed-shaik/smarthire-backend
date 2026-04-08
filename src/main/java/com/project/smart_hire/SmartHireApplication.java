package com.project.smart_hire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@EnableJpaRepositories
@SpringBootApplication
public class SmartHireApplication {

	public static void main(String[] args) {
		// Set default profile if none specified
		System.setProperty("spring.profiles.default", "default");
		
		SpringApplication app = new SpringApplication(SmartHireApplication.class);
		
		// Add application listeners for startup events
		app.addListeners(event -> {
			System.out.println("=== APPLICATION EVENT: " + event.getClass().getSimpleName());
		});
		
		try {
			app.run(args);
		} catch (Exception e) {
			System.err.println("=== APPLICATION STARTUP FAILED ===");
			e.printStackTrace();
			
			// Try failover profile
			System.setProperty("spring.profiles.active", "failover");
			System.out.println("=== RETRYING WITH FAILOVER PROFILE ===");
			SpringApplication.run(SmartHireApplication.class, args);
		}
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
