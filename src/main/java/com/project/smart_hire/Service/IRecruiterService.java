package com.project.smart_hire.Service;


import com.project.smart_hire.Entity.Recruiter;

import java.util.Optional;

public interface IRecruiterService {
    Optional<Recruiter> loginRecruiter(String email, String password);
    Optional<Recruiter> getRecruiterById(Long id);
    Recruiter updateRecruiterProfile(Recruiter recruiter, Long id);
}
