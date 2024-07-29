package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeesRepository extends JpaRepository<Employees, Long> {
    // 추가적인 쿼리 메서드를 정의할 수 있습니다.
    List<Employees> findByNameContainingIgnoreCase(String name);

    // 특정 프로젝트 요구사항에 부합하는 사원 필터링
    @Query("SELECT DISTINCT e FROM Employees e " +
            "JOIN e.skills es " +
            "JOIN ProjectRequirements pr ON pr.skill.skillId = es.skill.skillId " +
            "WHERE pr.project.projectId = :projectId " +
            "AND es.skillCareer >= pr.requiredExperience")
    List<Employees> findEmployeesByProjectRequirements(@Param("projectId") Long projectId);

}
