package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {
    List<Projects> findByStatus(int status);

    @Query("UPDATE Projects p SET p.status = :num WHERE p.projectId = :id")
    @Modifying
    @Transactional
    void updateStatusTo(Long id, int num);
    
}
