package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EmployeesRequest {
    private Long employeeId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;
    private String preferredLanguage;
    private String preferredProjectType;
    private String contactNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    private MultipartFile imageFile;
    private String image;
    private Double starPointAverage;
    private int allocation = -1;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate previousProjectEndDate;

    private List<EmployeesSkillRequest> employeesSkillRequests;

    private String existingImage;

    public Employees toEntity() throws IOException {
        Employees employee = new Employees();
        employee.setEmployeeId(this.employeeId);
        employee.setName(this.name);
        employee.setAddress(this.address);
        employee.setLatitude(this.latitude);
        employee.setLongitude(this.longitude);
        employee.setLastProjectEndDate(this.lastProjectEndDate);
        employee.setCurrentProjectEndDate(this.currentProjectEndDate);
        employee.setPreferredLanguage(Long.parseLong(this.preferredLanguage));
        employee.setPreferredProjectType(Long.parseLong(this.preferredProjectType));
        employee.setContactNumber(this.contactNumber);
        employee.setHireDate(this.hireDate);
        employee.setImage(this.image);
        employee.setStarPointAverage(this.starPointAverage);
        employee.setAllocation(this.allocation);
        return employee;
    }

    public Employees toEntity(String imgPath) throws IOException {
        this.setImage(imgPath);
        return this.toEntity();
    }

    public String getExistingImage() {
        return existingImage;
    }

    public EmployeesRequest() {
    }

    public EmployeesRequest(Long employeeId, String name, Double starPointAverage, LocalDate currentProjectEndDate, LocalDate lastProjectEndDate, LocalDate hireDate, int allocation) {
        this.employeeId = employeeId;
        this.name = name;
        this.starPointAverage = starPointAverage;
        this.currentProjectEndDate = currentProjectEndDate;
        this.lastProjectEndDate = lastProjectEndDate;
        this.hireDate = hireDate;
        this.allocation = allocation;
    }

}
