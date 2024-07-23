package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;

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
    private byte[] photo;
    private Long preferredLanguage;
    private Long preferredProjectType;
    private String contactNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    public Employees toEntity() {
        Employees employees = new Employees();
        employees.setEmployeeId(this.employeeId);
        employees.setName(this.name);
        employees.setAddress(this.address);
        employees.setLatitude(this.latitude);
        employees.setLongitude(this.longitude);
        employees.setLastProjectEndDate(this.lastProjectEndDate);
        employees.setCurrentProjectEndDate(this.currentProjectEndDate);
        employees.setPhoto(this.photo); // photo를 byte[]로 변환
        employees.setPreferredLanguage(this.preferredLanguage);
        employees.setPreferredProjectType(this.preferredProjectType);
        employees.setContactNumber(this.contactNumber);
        employees.setHireDate(this.hireDate);
        return employees;
    }
}
