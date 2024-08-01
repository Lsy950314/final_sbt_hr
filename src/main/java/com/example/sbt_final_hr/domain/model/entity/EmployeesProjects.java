package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees_projects", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "project_id", "skill_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeesProjects {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_projects_seq")
    @SequenceGenerator(name = "employees_projects_seq", sequenceName = "employees_projects_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employees employee;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skills skill;

    @Column(name = "registration_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registrationDate;

    @Column(name ="project_duration", nullable = false)
    private Long projectDuration;
    @Column(name ="star_point", nullable = true)
    private Double starPoint; //별점요소 추가
}
