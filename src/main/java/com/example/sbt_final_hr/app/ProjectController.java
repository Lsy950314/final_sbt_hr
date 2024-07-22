package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.ProjectsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectsService projectsService;

    public ProjectController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping("/createProject")
    public String createProject (Model model){
        ProjectsRequest projectsRequest = new ProjectsRequest();
        model.addAttribute("projectsRequest", projectsRequest);
        return "project/insertProject";
    }

    @PostMapping("/createProject")
    public void createProject(@ModelAttribute("projectsRequest") ProjectsRequest projectsRequest, Model model) {
        Projects project = projectsService.createProject(projectsRequest);
        model.addAttribute("project", project);
    }

}
