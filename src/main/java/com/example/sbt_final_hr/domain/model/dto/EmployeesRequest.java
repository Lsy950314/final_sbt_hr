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
    private Long employeeId;  // ID 필드 추가
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
    private String image;  // 이미지 경로 필드 추가
    private List<EmployeesSkillRequest> employeesSkillRequests;

    private String existingImage; //사진 수정 때문에

    public Employees toEntity() throws IOException {
        Employees employee = new Employees();
        employee.setEmployeeId(this.employeeId); // ID 설정
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
        employee.setImage(this.image);  // 이미지 설정
        return employee;
    }

    public Employees toEntity(String imgPath) throws IOException {
        this.setImage(imgPath); // 이미지를 설정합니다.
        return this.toEntity();
    }
    //7월 31일 10:20 사진 관련 작업중
    public String getExistingImage() {
        return existingImage;
    }

    public void setExistingImage(String existingImage) {
        this.existingImage = existingImage;
    }
    //7월 31일 10:20 사진 관련 작업중




}
