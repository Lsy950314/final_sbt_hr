package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {
    List<Projects> findByStatus(int status);



}
