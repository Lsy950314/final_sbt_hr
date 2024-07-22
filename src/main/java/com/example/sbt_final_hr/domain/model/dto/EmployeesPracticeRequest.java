package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.EmployeesPractice;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeesPracticeRequest {
    private Long employeeId;
    private String name;
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;

    public EmployeesPractice toEntity() {
        EmployeesPractice employees_practice = new EmployeesPractice();
        employees_practice.setEmployeeId(this.employeeId);
        employees_practice.setName(this.name);
        employees_practice.setAddress(this.address);
        employees_practice.setLastProjectEndDate(this.lastProjectEndDate);
        employees_practice.setCurrentProjectEndDate(this.currentProjectEndDate);
        return employees_practice;
    }



}
