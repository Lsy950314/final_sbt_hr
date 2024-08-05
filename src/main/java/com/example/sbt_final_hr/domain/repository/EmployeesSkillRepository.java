package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeesSkillRepository extends JpaRepository<EmployeesSkill, Long>{
    List<EmployeesSkill> findByEmployeeEmployeeId(Long employeeId);
//    void deleteByEmployeeEmployeeId(Long employeeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmployeesSkill es WHERE es.employee.employeeId = :employeeId")
    void deleteByEmployeeEmployeeId(@Param("employeeId") Long employeeId);

    //8월 3일 21:12 시도중
    @Modifying
    @Transactional
    @Query("UPDATE EmployeesSkill es SET es.skillCareer = es.skillCareer + :projectDuration WHERE es.employee.employeeId = :employeeId AND es.skill.skillId = :skillId")
    void updateSkillCareerOfProjectParticipants(@Param("employeeId") Long employeeId, @Param("skillId") Long skillId, @Param("projectDuration") double projectDuration);





}
