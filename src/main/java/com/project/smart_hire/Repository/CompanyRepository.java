package com.project.smart_hire.Repository;

import com.project.smart_hire.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByRecruiterId(Long recruiterId);
}