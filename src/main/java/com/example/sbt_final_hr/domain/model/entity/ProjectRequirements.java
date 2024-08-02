package com.example.sbt_final_hr.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_requirements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "skill_id"})
})
@Getter
@Setter
public class ProjectRequirements {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_requirements_seq")
    @SequenceGenerator(name = "project_requirements_seq", sequenceName = "project_requirements_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skills skill;

    @Column(name = "required_experience", nullable = false)
    private int requiredExperience;

    @Column(name = "required_count", nullable = false)
    private int requiredCount;

    @Column(name = "fulfilled_count", nullable = false)
    private int fulfilledCount = 0;
}
