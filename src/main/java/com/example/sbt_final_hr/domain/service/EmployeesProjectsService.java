package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository) {
        this.employeesProjectsRepository = employeesProjectsRepository;
    }

    public void createEmployeesProjects(EmployeesProjectsRequest employeesProjectsRequest) {
        employeesProjectsRepository.save(employeesProjectsRequest.toEntity());
    }

}
