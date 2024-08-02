package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
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

    public ProjectRequirements getRequirementsById(Long id) {
        return projectRequirementsRepository.findById(id).orElseThrow();
    }

    public List<ProjectRequirements> getRequirementsByProjectId(Long projectId) {
        return projectRequirementsRepository.findByProject_ProjectId(projectId);
    }

    public void createProjectRequirements(ProjectRequirements projectRequirements) {
        projectRequirementsRepository.save(projectRequirements);
    }

    public boolean updateFulfilledCount(Long projectRequirementId) {
        ProjectRequirements projectRequirements = projectRequirementsRepository.findById(projectRequirementId).orElseThrow(() -> new RuntimeException("Project requirements not found"));
        System.out.println(projectRequirements.getRequiredCount());
        System.out.println(projectRequirements.getFulfilledCount());
        if (projectRequirements.getRequiredCount() != projectRequirements.getFulfilledCount()) {
            System.out.println(projectRequirements.getRequiredCount());
            projectRequirements.setFulfilledCount(projectRequirements.getFulfilledCount() + 1);
            return true;
        } else {
            throw new RuntimeException("이미 충족된 요구사항입니다");
        }

    }

    public boolean checkFulfilledCount(Long projectId) {
        List<ProjectRequirements> prList = this.getRequirementsByProjectId(projectId);
        for (ProjectRequirements pr : prList) {
            if (pr.getFulfilledCount() != pr.getRequiredCount()) {
                return false;
            }
        }
        return true;
    }

    public void deleteByProjectId(Long projectId) {
        List<ProjectRequirements> requirements = projectRequirementsRepository.findByProject_ProjectId(projectId);
        projectRequirementsRepository.deleteAll(requirements);
    }
}

