package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees_practice;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Employees_practiceRequest {
    private Long employeeID;
    private String name;
    private String address;
    private Date lastProjectEndDate;
    private Date currentProjectEndDate;

    public Employees_practice toEntity() {
        Employees_practice employees_practice = new Employees_practice();
        employees_practice.setEmployeeID(employeeID);
        employees_practice.setName(name);
        employees_practice.setAddress(address);
        employees_practice.setLastProjectEndDate(lastProjectEndDate);
        employees_practice.setCurrentProjectEndDate(currentProjectEndDate);
        return employees_practice;
    }

}
