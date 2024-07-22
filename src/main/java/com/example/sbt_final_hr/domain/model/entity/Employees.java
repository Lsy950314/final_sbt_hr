package com.example.sbt_final_hr.domain.model.entity;

import com.example.sbt_final_hr.domain.model.dto.EmployeesPracticeRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="Employees")
public class Employees {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_seq")
    @SequenceGenerator(name = "employees_seq", sequenceName = "EMPLOYEES_SEQ", allocationSize = 1)
    private Long employeeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;


    public EmployeesRequest toDto() {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        employeesRequest.setEmployeeId(this.employeeId);
        employeesRequest.setName(this.name);
        employeesRequest.setAddress(this.address);
        employeesRequest.setLatitude(this.latitude);
        employeesRequest.setLongitude(this.longitude);
        employeesRequest.setLastProjectEndDate(this.lastProjectEndDate);
        employeesRequest.setCurrentProjectEndDate(this.currentProjectEndDate);
        return employeesRequest;
    }

}
