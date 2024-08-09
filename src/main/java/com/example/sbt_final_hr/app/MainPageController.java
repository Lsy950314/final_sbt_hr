package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.service.ProjectsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    private final ProjectsService projectsService;

    public MainPageController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping("/index")
    public String getMainPage(Model model) {
        model.addAttribute("countProjects", projectsService.getCountProjects());
        return "mainPage/index";
    }
}
