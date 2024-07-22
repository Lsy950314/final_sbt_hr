package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectsRequest {

    private Long projectId;
    private String projectName;
    private String workLocation;
    private String clientCompany;
    private LocalDate startDate;
    private LocalDate endDate;
    private int status = -1;
    private Double latitude;
    private Double longitude;
    private String contactPhone;
    private String contactName;
    private LocalDateTime registrationDate;
    private Long projectTypeId;

    public Projects toEntity(ProjectTypes projectType) {
        Projects project = new Projects();
        project.setProjectId(this.projectId);
        project.setProjectName(this.projectName);
        project.setWorkLocation(this.workLocation);
        project.setClientCompany(this.clientCompany);
        project.setStartDate(this.startDate);
        project.setEndDate(this.endDate);
        project.setStatus(this.status);
        project.setLatitude(this.latitude);
        project.setLongitude(this.longitude);
        project.setContactPhone(this.contactPhone != null ? this.contactPhone : "010-1234-5678");
        project.setContactName(this.contactName != null ? this.contactName : "担当者");
        project.setRegistrationDate(this.registrationDate);
        project.setProjectType(projectType);
        return project;
    }
}
