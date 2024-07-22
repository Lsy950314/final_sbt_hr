package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.ProjectTypesRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;
    private final ProjectTypesRepository projectTypesRepository;

    public ProjectsService(ProjectsRepository projectsRepository, ProjectTypesRepository projectTypesRepository) {
        this.projectsRepository = projectsRepository;
        this.projectTypesRepository = projectTypesRepository;
    }

    public List<Projects> getAllProjects() {
       return projectsRepository.findAll();
    }

    public Projects createProject(ProjectsRequest projectsRequest) {
        ProjectTypes projectType = projectTypesRepository.findById(projectsRequest.getProjectTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid project type ID"));

        // 기본 값 설정
        if (projectsRequest.getContactPhone() == null) {
            projectsRequest.setContactPhone("010-1234-5678");
        }
        if (projectsRequest.getContactName() == null) {
            projectsRequest.setContactName("担当者");
        }

        Projects project = projectsRequest.toEntity(projectType);
        return projectsRepository.save(project);
    }

}
