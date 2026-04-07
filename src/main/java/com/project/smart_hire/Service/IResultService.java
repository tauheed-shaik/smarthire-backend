package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.Result;

import java.util.Optional;

public interface IResultService {
    Result getResultBySession(Long sessionId);

    Optional<Result> findBySessionId(Long sessionId);
}