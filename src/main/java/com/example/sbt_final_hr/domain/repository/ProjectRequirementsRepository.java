package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRequirementsRepository extends JpaRepository<ProjectRequirements, Long> {
    List<ProjectRequirements> findByProject_ProjectId(Long projectId);

    Optional<ProjectRequirements> findByProject_ProjectIdAndSkill_SkillId(Long projectId, Long skillId);

    ProjectRequirements findByProject_ProjectIdAndSkill_SkillIdAndRequiredExperience(Long project_projectId, Long skill_skillId, int requiredExperience);
}
