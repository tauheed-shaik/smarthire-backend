package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.Company;
import java.util.List;

public interface ICompanyService {
    Company addCompany(Company company);
    List<Company> getCompaniesByRecruiter(Long recruiterId);

    Company save(Company company);

    List<Company> findByRecruiterId(Long recruiterId);
}