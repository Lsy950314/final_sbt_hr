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
public class EmployeesPractice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "EMPLOYEE_SEQ", allocationSize = 1)
    private Long employeeId;

    private String name;
    private String address;

    @Column(name= "last_project_end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastProjectEndDate;

    @Column(name= "current_project_end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentProjectEndDate;
    @Lob
    private byte[] photo;

    @Column(name = "preferred_language")
    private Integer preferredLanguage;

    @Column(name = "preferred_project_type")
    private Integer preferredProjectType;

    private String contactNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    @ManyToOne
    @JoinColumn(name = "preferred_language", insertable = false, updatable = false)
    private Skill skill;

    @ManyToOne
    @JoinColumn(name = "preferred_project_type", insertable = false, updatable = false)
    private ProjectType projectType;

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
        employeesRequest.setPreferredLanguage(this.preferredLanguage);
        employeesRequest.setPreferredProjectType(this.preferredProjectType);
        employeesRequest.setContactNumber(this.contactNumber);
        employeesRequest.setHireDate(this.hireDate);
        return employeesRequest;
    }


}
