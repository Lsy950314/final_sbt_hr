package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    private final EmployeesRepository employeesRepository;
    private final EmployeesService employeesService;
    private final ProjectsService projectsService;
    private final ProjectRequirementsService projectRequirementsService;

    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository, EmployeesRepository employeesRepository, EmployeesService employeesService, ProjectsService projectsService, ProjectRequirementsService projectRequirementsService) {
        this.employeesProjectsRepository = employeesProjectsRepository;
        this.employeesRepository = employeesRepository;
        this.employeesService = employeesService;
        this.projectsService = projectsService;
        this.projectRequirementsService = projectRequirementsService;
    }

    public List<EmployeesProjects> getAllEmployeesProjects() {
        return employeesProjectsRepository.findAll();
    }

    public void insertEmployeesProjects(Long employeeId, Long projectsId, Long projectRequirementId) {
        Projects projects = projectsService.getProjectById(projectsId);
        long projectDuration = ChronoUnit.DAYS.between(projects.getStartDate(), projects.getEndDate());

        EmployeesProjectsRequest employeesProjectsRequest = new EmployeesProjectsRequest();

        employeesProjectsRequest.setEmployee(employeesService.findById(employeeId).get());
        employeesProjectsRequest.setProject(projects);
        employeesProjectsRequest.setRegistrationDate(LocalDateTime.now());
        employeesProjectsRequest.setStarPoint(null);
        employeesProjectsRequest.setProjectDuration(projectDuration);
        employeesProjectsRequest.setSkill(projectRequirementsService.getRequirementsById(projectRequirementId).getSkill());

        employeesProjectsRepository.save(employeesProjectsRequest.toEntity());

    }

    public List<EmployeesProjects> getEmployeesProjectByProjectId(Long id) {
        return employeesProjectsRepository.findByProject_ProjectId(id);
    }

    public List<Employees> getEmployeesByProjectId(Long projectId) {
        List<EmployeesProjects> employeesProjects = employeesProjectsRepository.findByProject_ProjectId(projectId);
        List<Employees> employees = new ArrayList<>();
        for (EmployeesProjects ep : employeesProjects) {
            employees.add(employeesRepository.findById(ep.getEmployee().getEmployeeId()).orElse(null)); // 사원 정보 조회
        }
        return employees;
    }


}
