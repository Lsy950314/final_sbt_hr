package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectRequirementsService {
    private final ProjectRequirementsRepository projectRequirementsRepository;
    private final ProjectsRepository projectsRepository;
    private final SkillsRepository skillsRepository;

    public ProjectRequirementsService(ProjectRequirementsRepository projectRequirementsRepository, ProjectsRepository projectsRepository, SkillsRepository skillsRepository) {
        this.projectRequirementsRepository = projectRequirementsRepository;
        this.projectsRepository = projectsRepository;
        this.skillsRepository = skillsRepository;
    }

    public List<ProjectRequirements> getRequirementsByProjectId(Long projectId) {
        return projectRequirementsRepository.findByProject_ProjectId(projectId);
    }

    public void createProjectRequirements(ProjectRequirements projectRequirements) {
        projectRequirementsRepository.save(projectRequirements);
    }

    public void updateProjectRequirements(ProjectRequirementsRequest request, Projects project) {
        ProjectRequirements projectRequirements = request.toEntity(project);
        projectRequirementsRepository.save(projectRequirements);
    }

    public void deleteByProjectId(Long projectId) {
       List<ProjectRequirements> requirements = projectRequirementsRepository.findByProject_ProjectId(projectId);
        projectRequirementsRepository.deleteAll(requirements);
    }
}

