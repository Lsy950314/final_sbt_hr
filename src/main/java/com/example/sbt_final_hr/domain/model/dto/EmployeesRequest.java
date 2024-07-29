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
    private MultipartFile photo;
    //이 코드가 있어서 createemployee뷰에서 직원 프로그래밍 경력 관련 데이터들을 받아서 넘기는듯
    private List<EmployeesSkillRequest> employeesSkillRequests;

    public Employees toEntity() throws IOException {
        Employees employee = new Employees();
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
        if (this.photo != null && !this.photo.isEmpty()) {
            employee.setPhoto(this.photo.getBytes());
        }
        return employee;
    }
}
