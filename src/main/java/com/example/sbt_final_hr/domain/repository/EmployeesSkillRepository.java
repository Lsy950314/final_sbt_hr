package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeesSkillRepository extends JpaRepository<EmployeesSkill, Long>{
    List<EmployeesSkill> findByEmployeeEmployeeId(Long employeeId);
//    void deleteByEmployeeEmployeeId(Long employeeId);
    //7월 31일 13:39 추가
    @Modifying
    @Transactional
    @Query("DELETE FROM EmployeesSkill es WHERE es.employee.employeeId = :employeeId")
    void deleteByEmployeeEmployeeId(@Param("employeeId") Long employeeId);
}
