package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Candidate;
import com.project.smart_hire.Repository.CandidateRepository;
import com.project.smart_hire.Service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository repo;

    @Override
    public Optional<Candidate> getCandidateById(Long id) {
        return repo.findById(id);
    }
}
