package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectTypesRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final ProjectTypesRepository projectTypesRepository;
    private final SkillsRepository skillsRepository;
    private final ProjectRequirementsRepository projectRequirementsRepository;

    public ProjectsService(ProjectsRepository projectsRepository, ProjectTypesRepository projectTypesRepository, SkillsRepository skillsRepository, ProjectRequirementsRepository projectRequirementsRepository) {
        this.projectsRepository = projectsRepository;
        this.projectTypesRepository = projectTypesRepository;
        this.skillsRepository = skillsRepository;
        this.projectRequirementsRepository = projectRequirementsRepository;
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
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


    public void updateStatus(Long projectId) {
        projectsRepository.updateStatusTo1(projectId);
// 프로젝트 완료 버튼 클릭하면 ...
//    public void completeProject(Long projectId) {
//        // 프로젝트 완료 처리 로직
//        // 예: 프로젝트 상태 업데이트, 관련 직원 업데이트, 등등
//
//        // 예시: 프로젝트 상태 업데이트
//        Projects project = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
//        project.setStatus(2); // 상태를 완료로 변경
//        projectRepository.save(project);
//
//        // 관련 직원 업데이트
//        List<EmployeesProjects> employeesProjects = employeesProjectsRepository.findByProject_ProjectId(projectId);
//        for (EmployeesProjects ep : employeesProjects) {
//            ep.setStarPoint(5.0); // 예시: 모든 직원에게 별점 부여
//            employeesProjectsRepository.save(ep);
//        }
//
//        // 기타 필요한 로직들...

//8월 1일 17:44 시범적으로 시도중 : 프로젝트 완료 누르면 project 테이블에서 status 를 1 + 2로 바꾸기
//    public void updateProjectStatus(Long projectId, int status) {
//        Projects project = projectsRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
//        project.setStatus(status);
//        projectsRepository.save(project);
//    }


    }
}
