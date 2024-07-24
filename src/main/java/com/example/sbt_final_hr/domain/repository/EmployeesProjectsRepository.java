package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeesProjectsRepository extends JpaRepository<EmployeesProjects, Long> {
    public List<EmployeesProjects> findByProject_ProjectId(Long id);
}
