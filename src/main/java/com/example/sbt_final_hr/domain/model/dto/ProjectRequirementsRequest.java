package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequirementsRequest {
    private Long id;
    private Projects project;
    private Skills skill;
    private int requiredExperience;
    private int requiredCount;
    private int fulfilledCount;

    public ProjectRequirements toEntity(Projects project) {
        ProjectRequirements projectRequirements = new ProjectRequirements();
        projectRequirements.setId(this.id); // 업데이트 시 ID 설정
        projectRequirements.setProject(project);
        projectRequirements.setSkill(this.skill);
        projectRequirements.setRequiredExperience(this.requiredExperience);
        projectRequirements.setRequiredCount(this.requiredCount);
        projectRequirements.setFulfilledCount(this.fulfilledCount);
        return projectRequirements;
    }

    public void fromEntity(ProjectRequirements projectRequirements) {
        this.id = projectRequirements.getId();
        this.project = projectRequirements.getProject();
        this.skill = projectRequirements.getSkill();
        this.requiredExperience = projectRequirements.getRequiredExperience();
        this.requiredCount = projectRequirements.getRequiredCount();
        this.fulfilledCount = projectRequirements.getFulfilledCount();
    }
}
