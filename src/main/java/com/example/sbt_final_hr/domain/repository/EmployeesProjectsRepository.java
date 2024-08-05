package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeesProjectsRepository extends JpaRepository<EmployeesProjects, Long> {
    List<EmployeesProjects> findByProject_ProjectId(Long id);
    List<EmployeesProjects> findByEmployee_EmployeeId(Long employeeId);
    EmployeesProjects findByEmployee_EmployeeIdAndProject_ProjectIdAndId (Long employeeId, Long projectId, Long projectRequirementsId);

    @Modifying
    @Query("UPDATE EmployeesProjects ep SET ep.starPoint = :starPoint WHERE ep.employee.employeeId = :employeeId AND ep.project.projectId = :projectId")
    void updateStarPointOfProjectParticipants(@Param("employeeId") Long employeeId, @Param("projectId") Long projectId, @Param("starPoint") Double starPoint);

    @Query("SELECT AVG(ep.starPoint) FROM EmployeesProjects ep WHERE ep.employee.employeeId = :employeeId")
    Double calculateAverageStarPointByEmployeeId(@Param("employeeId") long employeeId);

}
