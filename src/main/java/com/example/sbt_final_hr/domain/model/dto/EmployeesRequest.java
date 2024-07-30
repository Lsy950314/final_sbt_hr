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
    //13:05 SEMI 참고해서 만드는중, chat gpt
    private MultipartFile imageFile;
    //13:05 SEMI 참고해서 만드는중, chat gpt
    private List<EmployeesSkillRequest> employeesSkillRequests;
//13:05 SEMI 참고해서 만드는중, chat gpt
//    public Employees toEntity() throws IOException {
//        Employees employee = new Employees();
//        employee.setName(this.name);
//        employee.setAddress(this.address);
//        employee.setLatitude(this.latitude);
//        employee.setLongitude(this.longitude);
//        employee.setLastProjectEndDate(this.lastProjectEndDate);
//        employee.setCurrentProjectEndDate(this.currentProjectEndDate);
//        employee.setPreferredLanguage(Long.parseLong(this.preferredLanguage));
//        employee.setPreferredProjectType(Long.parseLong(this.preferredProjectType));
//        employee.setContactNumber(this.contactNumber);
//        employee.setHireDate(this.hireDate);
//        return employee;
//    }
    //박살나면 이 코드로 복구
//13:05 SEMI 참고해서 만드는중, chat gpt
//13:05 SEMI 참고해서 만드는중, chat gpt
    public Employees toEntity() throws IOException {
        Employees employee = new Employees();
        employee.setEmployeeId(this.employeeId); // 추가추가추가추가추가추가추가
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
        return employee;
    }
    //13:05 SEMI 참고해서 만드는중, chat gpt
    // 이미지 경로를 받아서 엔티티로 변환하는 오버로딩 메서드
    public Employees toEntity(String imgPath) throws IOException {
        Employees employee = toEntity(); // 기본 메서드 호출
        employee.setImage(imgPath); // 이미지를 String으로 변환하여 설정
        return employee;
    }







}
