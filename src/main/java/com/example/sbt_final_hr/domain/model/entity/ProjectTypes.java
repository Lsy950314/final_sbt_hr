package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "project_types")
@Getter
@ToString
public class ProjectTypes {

    @Id
    @Column(name = "project_type_id")
    private Long projectTypeId;

    @Column(name = "project_type_name")
    private String projectTypeName;

}
