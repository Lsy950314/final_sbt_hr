package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

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
    //Employee테이블, Employee_Skill테이블 모두에 튜플 삽입 가능한 create 메서드 추가 시도중.
//    private List<EmployeesSkillRequest.SkillDTO> skills; // 프로그래밍 경력 -> 시도하는거 망하면 복구해야함
    private List<EmployeesSkillRequest> employeesskill;



    public Employees toEntity() {
        Employees employees = new Employees();
        employees.setEmployeeId(this.employeeId);
        employees.setName(this.name);
        employees.setAddress(this.address);
        employees.setLatitude(this.latitude);
        employees.setLongitude(this.longitude);
        employees.setLastProjectEndDate(this.lastProjectEndDate);
        employees.setCurrentProjectEndDate(this.currentProjectEndDate);
        employees.setPhoto(this.photo);
        employees.setPreferredLanguage(this.preferredLanguage);
        employees.setPreferredProjectType(this.preferredProjectType);
        employees.setContactNumber(this.contactNumber);
        employees.setHireDate(this.hireDate);
        return employees;
    }
}
