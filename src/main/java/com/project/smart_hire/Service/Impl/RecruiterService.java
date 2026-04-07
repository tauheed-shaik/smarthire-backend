package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Recruiter;
import com.project.smart_hire.Repository.RecruiterRepository;
import com.project.smart_hire.Service.IRecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterService implements IRecruiterService {

    @Autowired private RecruiterRepository repo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public Optional<Recruiter> loginRecruiter(String email, String password) {
        Optional<Recruiter> recruiter = repo.findByEmail(email);
        if (recruiter.isEmpty() || !encoder.matches(password, recruiter.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return recruiter;
    }

    @Override
    public Optional<Recruiter> getRecruiterById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Recruiter updateRecruiterProfile(Recruiter recruiter, Long id) {
        Recruiter existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Recruiter not found"));
        existing.setName(recruiter.getName());
        existing.setEmail(recruiter.getEmail());
        existing.setCompany(recruiter.getCompany());
        existing.setPhone(recruiter.getPhone());
        if (recruiter.getPassword() != null && !recruiter.getPassword().isBlank()) {
            existing.setPassword(encoder.encode(recruiter.getPassword()));
        }
        return repo.save(existing);
    }
}