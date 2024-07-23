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

    public EmployeesProjects toEntity() {
        EmployeesProjects employeesProjects = new EmployeesProjects();
        employeesProjects.setEmployee(this.employee);
        employeesProjects.setProject(this.project);
        employeesProjects.setSkill(this.skill);
        employeesProjects.setRegistrationDate(LocalDateTime.now());
        return employeesProjects;
    }
}
