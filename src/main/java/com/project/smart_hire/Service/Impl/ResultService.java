// src/main/java/com/project/smart_hire/Service/ResultService.java
package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Result;
import com.project.smart_hire.Repository.ResultRepository;
import com.project.smart_hire.Service.IResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultService implements IResultService {

    @Autowired private ResultRepository repo;

    @Override
    public Result getResultBySession(Long sessionId) {
        return repo.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Result not found for session: " + sessionId));
    }

    @Override
    public Optional<Result> findBySessionId(Long sessionId) {
        return repo.findBySessionId(sessionId);
    }
}