package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ProjectsRequest {

    private Long projectId;
    private String projectName;
    private String workLocation;
    private String clientCompany;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private int status = -1;
    private Double latitude;
    private Double longitude;
    private String contactPhone;
    private String contactName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime registrationDate;
    private Long projectTypeId;

    private List<ProjectRequirementsRequest> projectRequirements; // 요구스킬 스택을 위한 추가 부분
    
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

    public void fromEntity(Projects project) {
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.workLocation = project.getWorkLocation();
        this.clientCompany = project.getClientCompany();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        this.status = project.getStatus();
        this.latitude = project.getLatitude();
        this.longitude = project.getLongitude();
        this.contactPhone = project.getContactPhone();
        this.contactName = project.getContactName();
        this.registrationDate = project.getRegistrationDate();
        this.projectTypeId = project.getProjectType().getProjectTypeId();
    }
}
