package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Skills {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_seq")
    @SequenceGenerator(name = "skill_seq", sequenceName = "skill_sequence", allocationSize = 1)
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "skill_name")
    private String skillName;
    }
