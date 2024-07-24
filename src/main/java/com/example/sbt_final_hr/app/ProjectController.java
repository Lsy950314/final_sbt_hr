package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.service.ProjectRequirementsService;
import com.example.sbt_final_hr.domain.service.ProjectTypesService;
import com.example.sbt_final_hr.domain.service.ProjectsService;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController {
    private final ProjectsService projectsService;
    private final ProjectTypesService projectTypesService;
    private final ProjectRequirementsService projectRequirementsService;
    private final SkillsService skillsService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public ProjectController(ProjectsService projectsService, ProjectTypesService projectTypesService, ProjectRequirementsService projectRequirementsService, SkillsService skillsService) {
        this.projectsService = projectsService;
        this.projectTypesService = projectTypesService;
        this.projectRequirementsService = projectRequirementsService;
        this.skillsService = skillsService;
    }

    @GetMapping("/readAllProjects")
    public String readAllProjects(Model model) {
        model.addAttribute("projects", projectsService.getAllProjects());
        return "project/readAllProjects"; // 가상의 주소
    }

    @GetMapping("/createProject")
    public String createProject(Model model) {
        ProjectsRequest projectsRequest = new ProjectsRequest();
        model.addAttribute("projectsRequest", projectsRequest);
        model.addAttribute("apiKey", apiKey);
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "project/insertProject";
    }

    @PostMapping("/createProject")
    public String createProject(@ModelAttribute ProjectsRequest projectsRequest) {
        Projects project = projectsService.createProject(projectsRequest);

        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                projectRequirementsService.createProjectRequirements(projectRequirements);
            }
        }

        return "redirect:/createProject";
    }

    @GetMapping("/updateProject")
    public String updateProject(@RequestParam Map<String, String> payload, Model model) {
        long id = Long.parseLong(payload.get("id"));
        Projects projects = projectsService.getProjectById(id);
        ProjectsRequest projectsRequest = new ProjectsRequest();
        projectsRequest.fromEntity(projects);
        projectsRequest.setProjectRequirements(projectRequirementsService.getRequirementsByProjectId(id)
                .stream()
                .map(requirements -> {
                    ProjectRequirementsRequest request = new ProjectRequirementsRequest();
                    request.fromEntity(requirements);
                    return request;
                }).collect(Collectors.toList()));

        model.addAttribute("projectsRequest", projectsRequest);
        model.addAttribute("apiKey", apiKey);
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "project/updateProject";
    }

    @PostMapping("/updateProject")
    public String updateProject(@ModelAttribute("projectsRequest") ProjectsRequest projectsRequest) {
        projectsService.updateProject(projectsRequest);

        Projects project = projectsRequest.toEntity(projectTypesService.getProjectTypeById(projectsRequest.getProjectTypeId()));
        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                if (requirementsRequest.getId() != null) {
                    projectRequirementsService.updateProjectRequirements(requirementsRequest, project);
                } else {
                    ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                    projectRequirementsService.createProjectRequirements(projectRequirements);
                }
            }
        }

        return "redirect:/updateProject?id=" + projectsRequest.getProjectId();
    }

    @GetMapping("/deleteProject")
    public String deleteProject(@RequestParam Map<String, String> payload, Model model) {
        long id = Long.parseLong(payload.get("id"));
        projectRequirementsService.deleteByProjectId(id);
        projectsService.deleteProject(id);
        System.out.println("삭제 성공");
        return "redirect:/createProject";
    }

}
