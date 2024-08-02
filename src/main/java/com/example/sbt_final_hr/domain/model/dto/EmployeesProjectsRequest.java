package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeesProjectsRequest {
    private Long id;
    private Employees employee;
    private Projects project;
    private Skills skill;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registrationDate;

    private Double projectDuration;
    private Double starPoint;

    public EmployeesProjects toEntity() {
        EmployeesProjects employeesProjects = new EmployeesProjects();
        employeesProjects.setEmployee(this.employee);
        employeesProjects.setProject(this.project);
        employeesProjects.setSkill(this.skill);
        employeesProjects.setRegistrationDate(LocalDateTime.now());
        employeesProjects.setProjectDuration(this.projectDuration);
        employeesProjects.setStarPoint(this.starPoint);
        return employeesProjects;
    }

    public EmployeesProjectsRequest fromEntity(EmployeesProjects employeesProjects) {
        EmployeesProjectsRequest employeesProjectsRequest = new EmployeesProjectsRequest();
        employeesProjectsRequest.setEmployee(employeesProjects.getEmployee());
        employeesProjectsRequest.setProject(employeesProjects.getProject());
        employeesProjectsRequest.setSkill(employeesProjects.getSkill());
        employeesProjectsRequest.setRegistrationDate(employeesProjects.getRegistrationDate());
        employeesProjectsRequest.setProjectDuration(employeesProjects.getProjectDuration());
        employeesProjectsRequest.setStarPoint(employeesProjects.getStarPoint());
        return employeesProjectsRequest;
    }
}
