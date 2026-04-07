package com.project.smart_hire.Config;

import com.project.smart_hire.Entity.Recruiter;
import com.project.smart_hire.Repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RecruiterRepository recruiterRepo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (recruiterRepo.findByEmail("recruiter@smarthire.com").isEmpty()) {
            Recruiter r = new Recruiter();
            r.setName("HR Manager");
            r.setEmail("recruiter@smarthire.com");
            r.setPassword(encoder.encode("recruiter123"));
            r.setCompany("SmartHire Inc");
            r.setPhone("9876543210");
            recruiterRepo.save(r);
            System.out.println("DEFAULT RECRUITER: recruiter@smarthire.com / recruiter123");
        }
    }
}