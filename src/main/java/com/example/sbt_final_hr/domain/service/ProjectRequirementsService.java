package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectRequirementsService {

    private final ProjectRequirementsRepository projectRequirementsRepository;

    public ProjectRequirementsService(ProjectRequirementsRepository projectRequirementsRepository) {
        this.projectRequirementsRepository = projectRequirementsRepository;
    }

    public List<ProjectRequirements> getAllProjectRequirements() {
        return projectRequirementsRepository.findAll();
    }

    public void createProjectRequirements(ProjectRequirements projectRequirements) {
        projectRequirementsRepository.save(projectRequirements);
    }
}
