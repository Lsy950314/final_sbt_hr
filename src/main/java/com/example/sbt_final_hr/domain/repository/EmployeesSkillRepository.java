package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesSkillRepository extends JpaRepository<EmployeesSkill, Long>{

}
