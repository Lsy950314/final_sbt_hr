package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRequirementsRepository extends JpaRepository<ProjectRequirements, Long> {
    List<ProjectRequirements> findByProject_ProjectId(Long projectId);
}
