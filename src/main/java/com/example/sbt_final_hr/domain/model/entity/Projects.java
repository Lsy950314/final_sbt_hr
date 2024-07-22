package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_sequence")
    @SequenceGenerator(name = "project_sequence", sequenceName = "project_sequence", allocationSize = 1)
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "work_location")
    private String workLocation;

    @Column(name = "client_company")
    private String clientCompany;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    private int status = -1;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone = "010-1234-5678";

    @Column(name = "contact_name", nullable = false)
    private String contactName = "担当者";

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne
    @JoinColumn(name = "project_type_id", nullable = false)
    private ProjectTypes projectType;
}
