package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillsRepository extends JpaRepository<Skills, Long> {

}
