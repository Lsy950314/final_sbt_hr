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
    private String image;
    private String contactNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;
    //별점 요소 추가(참여 안건 별 별점의 평균;employees_projects 튜플에 별점 생기면 db단계에서 자동계산됨)
    @Column(name= "star_point_average")
    private Double starPointAverage;


    @Column(name = "preferred_language")
    private Long preferredLanguage;
    @Column(name = "preferred_project_type")
    private Long preferredProjectType;
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
        employeesRequest.setEmployeeId(this.employeeId); // employeeId 추가
        employeesRequest.setName(this.name);
        employeesRequest.setAddress(this.address);
        employeesRequest.setLatitude(this.latitude);
        employeesRequest.setLongitude(this.longitude);
        employeesRequest.setLastProjectEndDate(this.lastProjectEndDate);
        employeesRequest.setCurrentProjectEndDate(this.currentProjectEndDate);
        employeesRequest.setPreferredLanguage(this.preferredLanguage.toString());
        employeesRequest.setPreferredProjectType(this.preferredProjectType.toString());
        employeesRequest.setContactNumber(this.contactNumber);
        employeesRequest.setHireDate(this.hireDate);
        employeesRequest.setImage(this.image);

        return employeesRequest;
    }








}
