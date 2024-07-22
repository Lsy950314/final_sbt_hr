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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "EMPLOYEE_SEQ", allocationSize = 1)
    private Long employeeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    @Column(name= "last_project_end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;
    @Column(name= "current_project_end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;
    @Lob
    private byte[] photo;
    @Column(name = "preferred_language")
    private Long preferredLanguage;

    @Column(name = "preferred_project_type")
    private Long preferredProjectType;

    private String contactNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;


    @ManyToOne
    @JoinColumn(name = "preferred_language", insertable = false, updatable = false)
    private Skills skill;

    @ManyToOne
    @JoinColumn(name = "preferred_project_type", insertable = false, updatable = false)
    private ProjectTypes projectType;

    public EmployeesRequest toDto() {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        employeesRequest.setEmployeeId(this.employeeId);
        employeesRequest.setName(this.name);
        employeesRequest.setAddress(this.address);
        employeesRequest.setLatitude(this.latitude);
        employeesRequest.setLongitude(this.longitude);
        employeesRequest.setLastProjectEndDate(this.lastProjectEndDate);
        employeesRequest.setCurrentProjectEndDate(this.currentProjectEndDate);
        employeesRequest.setPhoto(this.photo);
        //preferredLanguage랑 preferredProjectType은 타입을 Interger에서 Long으로 변경.(외래키로 선호 언어, 선호 업계 보여주기 위해서)
        employeesRequest.setPreferredLanguage(this.preferredLanguage);
        employeesRequest.setPreferredProjectType(this.preferredProjectType);
        employeesRequest.setContactNumber(this.contactNumber);
        employeesRequest.setHireDate(this.hireDate);
        return employeesRequest;
    }


}
