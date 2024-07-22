package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectTypesRequest {

    private Long projectTypeId;
    private String projectTypeName;

    public ProjectTypes toEntity(){
        ProjectTypes entity = new ProjectTypes();
        setProjectTypeId(entity.getProjectTypeId());
        setProjectTypeName(entity.getProjectTypeName());
        return entity;
    }

}
