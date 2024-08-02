package com.example.sbt_final_hr.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "projects")
@Getter
@Setter
@ToString
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime registrationDate;

    @ManyToOne
    @JoinColumn(name = "project_type_id", nullable = false)
    private ProjectTypes projectType;

    public Double getTotalProjectDurationInMonths() {

        if (startDate == null || endDate == null) {
            return 0.0;
        }

        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        LocalDate adjustedEndDate = startDate.plusMonths(monthsBetween);
        long daysRemaining = ChronoUnit.DAYS.between(adjustedEndDate, endDate);


        // 남은 일수를 개월 단위로 변환하여 합산
        return monthsBetween + (Double) (daysRemaining / 30.4375);
    }
}
