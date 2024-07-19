package com.example.sbt_final_hr.domain.model.dto;

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

//    public Employees_practice toEntity() {
//        Employees_practice employees_practice = new Employees_practice();
//        employees_practice.setEmployeeID(employeeID);
//        employees_practice.setName(name);
//        employees_practice.setAddress(address);
//        employees_practice.setLastProjectEndDate(lastProjectEndDate);
//        employees_practice.setCurrentProjectEndDate(currentProjectEndDate);
//        return employees_practice;
//    }



}
