package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final ProjectTypesRepository projectTypesRepository;
    private final EmployeesProjectsRepository employeesProjectsRepository;

    public ProjectsService(ProjectsRepository projectsRepository, ProjectTypesRepository projectTypesRepository, SkillsRepository skillsRepository, ProjectRequirementsRepository projectRequirementsRepository, EmployeesProjectsRepository employeesProjectsRepository) {
        this.projectsRepository = projectsRepository;
        this.projectTypesRepository = projectTypesRepository;
        this.employeesProjectsRepository = employeesProjectsRepository;
    }

    public Map<String, Integer> getCountProjects(){
        Map<String, Integer> countProjects = new HashMap<>();

        int totalProjects = projectsRepository.countAllProjects();
        int assignedProjects = projectsRepository.countAssignedProjects();
        int unassignedProjects = projectsRepository.countUnassignedProjects();

        countProjects.put("totalProjects", totalProjects);
        countProjects.put("assignedProjects", assignedProjects);
        countProjects.put("unassignedProjects", unassignedProjects);

        return countProjects;
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll(Sort.by(Sort.Direction.ASC, "projectName"));
    }

    public List<ProjectsRequest> getAllProjectsSummary() {
        return projectsRepository.findAllProjectsSummary();
    }

    public List<ProjectsRequest> getProjectByEmployee(Long employeeId){
     List<Projects> projects = employeesProjectsRepository.findProjectsByEmployeeId(employeeId);
     List<ProjectsRequest> projectsRequests;
        projectsRequests = projects.stream().map(p -> {
              ProjectsRequest pr = new ProjectsRequest();
              pr.fromEntity(p);
              return pr;
          }).collect(Collectors.toList());
        return projectsRequests;
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

    @Transactional
    public void updateStatusTo(Long projectId, int num) {
        projectsRepository.updateStatusTo(projectId, num);
    }

    public List<Projects> findByProjectIds(List<Long> projectIds) {
        return projectsRepository.findByProjectIdIn(projectIds);
    }

    //8월 5일 17:02 추가중
    public List<Projects> findRecentProjectsByIds(List<Long> projectIds) {
        List<Projects> projects = projectsRepository.findByProjectIdIn(projectIds);

        return projects.stream()
                .sorted(Comparator.comparing(Projects::getEndDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }



}
