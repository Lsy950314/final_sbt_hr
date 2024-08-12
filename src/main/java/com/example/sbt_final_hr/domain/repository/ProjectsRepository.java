package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import org.springframework.data.domain.Sort;
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
    void updateStatusTo(Long id, int num);

    List<Projects> findByProjectIdIn(List<Long> projectIds);

    @Query("SELECT new com.example.sbt_final_hr.domain.model.dto.ProjectsRequest(" +
            "p.projectId, p.projectName, p.clientCompany, p.startDate, p.endDate, p.status) " +
            "FROM Projects p ORDER BY p.registrationDate desc")
    List<ProjectsRequest> findAllProjectsSummary();

    @Query("SELECT COUNT(p) FROM Projects p")
    int countAllProjects(); // 총 프로젝트 개수

    @Query("SELECT COUNT(p) FROM Projects p WHERE p.status = 1")
    int countAssignedProjects(); // 현재 진행 중인 프로젝트 (배정된 프로젝트)

    @Query("SELECT COUNT(p) FROM Projects p WHERE p.status = -1")
    int countUnassignedProjects(); // 미배정 프로젝트 개수

}
