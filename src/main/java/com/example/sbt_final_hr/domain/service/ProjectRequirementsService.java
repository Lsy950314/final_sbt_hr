package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ProjectRequirements findByProjectIdAndSkillIdAndRequiredExperience(Long projectId, Long skillId, int requiredExperience) {
        return projectRequirementsRepository.findByProject_ProjectIdAndSkill_SkillIdAndRequiredExperience(projectId,
                skillId,
                requiredExperience);
    }

    public void createOrUpdateProjectRequirements(ProjectRequirements projectRequirements) {
        Optional<ProjectRequirements> existingRequirement = projectRequirementsRepository.findByProject_ProjectIdAndSkill_SkillId(
                projectRequirements.getProject().getProjectId(),
                projectRequirements.getSkill().getSkillId()
        );

        if (existingRequirement.isPresent()) {
            ProjectRequirements existing = existingRequirement.get();
            existing.setRequiredExperience(projectRequirements.getRequiredExperience());
            existing.setRequiredCount(projectRequirements.getRequiredCount());
            projectRequirementsRepository.save(existing);
        } else {
            projectRequirementsRepository.save(projectRequirements);
        }
    }

    public List<ProjectRequirements> findByProjectId(Long projectId) {
        return projectRequirementsRepository.findByProject_ProjectId(projectId);
    }

    public ProjectRequirements getRequirementsById(Long id) {
        return projectRequirementsRepository.findById(id).orElseThrow();
    }

    public List<ProjectRequirements> getRequirementsByProjectId(Long projectId) {
        List<ProjectRequirements> requirements = projectRequirementsRepository.findByProject_ProjectId(projectId);

        return requirements.stream()
                .sorted((req1, req2) -> {
                    boolean isReq1Fulfilled = req1.getFulfilledCount() == req1.getRequiredCount();
                    boolean isReq2Fulfilled = req2.getFulfilledCount() == req2.getRequiredCount();

                    if (isReq1Fulfilled && !isReq2Fulfilled) {
                        return 1;
                    } else if (!isReq1Fulfilled && isReq2Fulfilled) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }

    public void createProjectRequirements(ProjectRequirements projectRequirements) {
        projectRequirementsRepository.save(projectRequirements);
    }

    public boolean updateFulfilledCount(Long projectRequirementId) {
        ProjectRequirements projectRequirements = projectRequirementsRepository.findById(projectRequirementId).orElseThrow(() -> new RuntimeException("Project requirements not found"));
        if (projectRequirements.getRequiredCount() != projectRequirements.getFulfilledCount()) {
            projectRequirements.setFulfilledCount(projectRequirements.getFulfilledCount() + 1);
            return true;
        } else {
            throw new RuntimeException("こちらの要求はすでに満たされています。割り当てられた社員をキャンセルしてから新しい社員を割り当ててください。");
        }
    }

    public boolean decreaseFulfilledCount(Long projectRequirementsId) {
        ProjectRequirements pr = projectRequirementsRepository.findById(projectRequirementsId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        int fc = pr.getFulfilledCount();
        if (fc > 0) {
            pr.setFulfilledCount(fc - 1);
            return true;
        }
        return false;
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

    public void deleteById(Long id) {
        projectRequirementsRepository.deleteById(id);
    }
}

