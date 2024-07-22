package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "project_types")
@Getter
public class ProjectTypes {

    @Id
    @Column(name = "project_type_id")
    private Long projectTypeId;

    @Column(name = "project_type_name")
    private String projectTypeName;

}
