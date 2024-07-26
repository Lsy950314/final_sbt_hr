package com.example.sbt_final_hr.domain.model.entity;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeesSkill> skills;

    public EmployeesRequest toDto() {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        employeesRequest.setName(this.name);
        employeesRequest.setAddress(this.address);
        employeesRequest.setLatitude(this.latitude);
        employeesRequest.setLongitude(this.longitude);
        employeesRequest.setLastProjectEndDate(this.lastProjectEndDate);
        employeesRequest.setCurrentProjectEndDate(this.currentProjectEndDate);
        // Add conversion from byte[] to MultipartFile if needed
        employeesRequest.setPreferredLanguage(this.preferredLanguage.toString());
        employeesRequest.setPreferredProjectType(this.preferredProjectType.toString());
        employeesRequest.setContactNumber(this.contactNumber);
        employeesRequest.setHireDate(this.hireDate);
        return employeesRequest;
    }
}
