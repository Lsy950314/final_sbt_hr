package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "AND e.allocation = -1 " +
            "AND es.skillCareer >= pr.requiredExperience")
    List<Employees> findEmployeesByProjectRequirements(@Param("projectId") Long projectId);

    // 현재 프로젝트 종료일과 최근 프로젝트 종료일을 업데이트


    @Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.starPointAverage = :starPointAverage WHERE e.employeeId = :employeeId")
    void updateStarPointAverage(@Param("employeeId") long employeeId, @Param("starPointAverage") Double starPointAverage);

    @Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.lastProjectEndDate = e.currentProjectEndDate, e.currentProjectEndDate = null WHERE e.employeeId = :employeeId")
    void updateProjectEndDates(@Param("employeeId") long employeeId);

    //8월 6일 12:45 추가 사원 이름 기준 오름차순 select
    @Query("SELECT e FROM Employees e ORDER BY e.hireDate DESC")
    List<Employees> findAllOrderByEmployeeNameAsc();

    //사원 allocation 바꾸는 코드
    @Query("UPDATE Employees e SET e.allocation = :num WHERE e.employeeId = :id")
    @Modifying
    int updateAllocationTo(Long id, int num);

    //8월 9일 10:13 read기능 최적화 관련 시도중
    @Query("SELECT new com.example.sbt_final_hr.domain.model.dto.EmployeesRequest(" +
            "e.employeeId, e.name, e.starPointAverage, e.currentProjectEndDate, e.lastProjectEndDate, e.hireDate, e.allocation) " +
            "FROM Employees e ORDER BY e.hireDate DESC")
    List<EmployeesRequest> findAllEmployeesSummary();

    @Query("SELECT COUNT(e) FROM Employees e")
    int countAllEmployees(); // 총 사원 수

    @Query("SELECT COUNT(e) FROM Employees e WHERE e.allocation = 1")
    int countAssignedEmployees(); // 현재 프로젝트 진행 중인 사원 수

    @Query("SELECT COUNT(e) FROM Employees e WHERE e.allocation =-1")
    int countUnassignedEmployees(); // 현재 대기중인 사원 수



}
