package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final ProjectTypesRepository projectTypesRepository;
    private final SkillsRepository skillsRepository;
    private final ProjectRequirementsRepository projectRequirementsRepository;
    private final EmployeesProjectsRepository employeesProjectsRepository;

    public ProjectsService(ProjectsRepository projectsRepository, ProjectTypesRepository projectTypesRepository, SkillsRepository skillsRepository, ProjectRequirementsRepository projectRequirementsRepository, EmployeesProjectsRepository employeesProjectsRepository) {
        this.projectsRepository = projectsRepository;
        this.projectTypesRepository = projectTypesRepository;
        this.skillsRepository = skillsRepository;
        this.projectRequirementsRepository = projectRequirementsRepository;
        this.employeesProjectsRepository = employeesProjectsRepository;
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }

    public List<Projects> getProjectByEmployee(Long employeeId){
        return employeesProjectsRepository.findProjectsByEmployeeId(employeeId);
    }

    public List<Projects> getAssignedProjects() {
        return projectsRepository.findByStatus(1);
    }

    public void deleteProject(Long id) {
        projectsRepository.deleteById(id);
    }

    public Projects getProjectById(Long id) {
        return projectsRepository.findById(id).orElseThrow();
    }

    public Projects createProject(ProjectsRequest projectsRequest) {
        ProjectTypes projectType = projectTypesRepository.findById(projectsRequest.getProjectTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid project type ID"));
        projectsRequest.setRegistrationDate(LocalDateTime.now());

        Projects project = projectsRequest.toEntity(projectType);
        projectsRepository.save(project);
        return project;
    }

    public Projects updateProject(ProjectsRequest projectsRequest) {
        ProjectTypes projectType = projectTypesRepository.findById(projectsRequest.getProjectTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid project type ID"));

        // 기존 프로젝트를 찾고 등록일은 변경하지 않도록 설정
        Projects existingProject = projectsRepository.findById(projectsRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectsRequest.getProjectId()));

        Projects project = projectsRequest.toEntity(projectType);
        project.setRegistrationDate(existingProject.getRegistrationDate()); // 기존 등록일 유지
        projectsRepository.save(project);
        System.out.println("업데이트 성공");
        return existingProject;
    }

    public void updateStatusTo(Long projectId, int num) {
        projectsRepository.updateStatusTo(projectId, num);
    }

    public List<Projects> findByProjectIds(List<Long> projectIds) {
        return projectsRepository.findByProjectIdIn(projectIds);
    }



}
