package com.project.smart_hire.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private Frontend frontend = new Frontend();

    public static class Frontend {
        private String baseUrl;
        private Candidate candidate = new Candidate();

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

        public Candidate getCandidate() { return candidate; }
        public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    }

    public static class Candidate {
        private String interviewPath;
        public String getInterviewPath() { return interviewPath; }
        public void setInterviewPath(String interviewPath) { this.interviewPath = interviewPath; }
    }

    public Frontend getFrontend() { return frontend; }
    public void setFrontend(Frontend frontend) { this.frontend = frontend; }
}