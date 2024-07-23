package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequirementsRequest {
    private Skills skill;
    private int requiredExperience;
    private int requiredCount;

    public ProjectRequirements toEntity(Projects project) {
        ProjectRequirements projectRequirements = new ProjectRequirements();
        projectRequirements.setProject(project);
        projectRequirements.setSkill(this.skill);
        projectRequirements.setRequiredExperience(this.requiredExperience);
        projectRequirements.setRequiredCount(this.requiredCount);
        projectRequirements.setFulfilledCount(0); // 초기값 0 설정
        return projectRequirements;
    }
}
