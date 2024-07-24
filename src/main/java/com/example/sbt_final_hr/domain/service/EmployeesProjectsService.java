package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository) {
        this.employeesProjectsRepository = employeesProjectsRepository;
    }

    public List<EmployeesProjects> getAllEmployeesProjects() {
        return employeesProjectsRepository.findAll();
    }

    public List<EmployeesProjects> getEmployeesProjectByProjectId(Long id) {
        return employeesProjectsRepository.findByProject_ProjectId(id);
    }

}
