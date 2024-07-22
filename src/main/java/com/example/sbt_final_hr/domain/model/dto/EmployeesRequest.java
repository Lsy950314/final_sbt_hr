package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeesRequest {
    private Long employeeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;

    public Employees toEntity() {
        Employees employees = new Employees();
        employees.setEmployeeId(this.employeeId);
        employees.setName(this.name);
        employees.setAddress(this.address);
        employees.setLatitude(this.latitude);
        employees.setLongitude(this.longitude);
        employees.setLastProjectEndDate(this.lastProjectEndDate);
        employees.setCurrentProjectEndDate(this.currentProjectEndDate);
        return employees;
    }



}
