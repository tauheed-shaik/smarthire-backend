package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Company;
import com.project.smart_hire.Repository.CompanyRepository;
import com.project.smart_hire.Service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    private CompanyRepository repo;

    @Override
    public Company addCompany(Company company) {
        return repo.save(company);
    }

    @Override
    public List<Company> getCompaniesByRecruiter(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }

    @Override
    public Company save(Company company) {
        return repo.save(company);
    }

    @Override
    public List<Company> findByRecruiterId(Long recruiterId) {
        return repo.findByRecruiterId(recruiterId);
    }
}