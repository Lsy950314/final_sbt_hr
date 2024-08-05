package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeesProjectsRequest {
    private Long id;

    @JsonIgnore
    private Employees employee;

    private Projects project;
    private Skills skill;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registrationDate;

    private Double projectDuration;
    private Double starPoint;

    private String employeeName;
    //8월 2일 17:02
//    private Long employeeId;
    //8월 2일 17:02
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

    public EmployeesProjectsRequest fromEntity(EmployeesProjects entity) {
        this.employee = entity.getEmployee();
        this.project = entity.getProject();
        this.skill = entity.getSkill();
        this.registrationDate = entity.getRegistrationDate();
        this.projectDuration = entity.getProjectDuration();
        this.starPoint = entity.getStarPoint();
        if (entity.getEmployee() != null) {
            this.employeeName = entity.getEmployee().getName();
//            this.employeeId = entity.getEmployee().getEmployeeId();
        }
        return this;
    }
}
