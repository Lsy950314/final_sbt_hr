package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.ProjectsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {
    private final ProjectsService projectsService;
    private final EmployeesService employeesService;

    public MainPageController(ProjectsService projectsService, EmployeesService employeesService) {
        this.projectsService = projectsService;
        this.employeesService = employeesService;
    }

    @GetMapping("/index")
    public String getMainPage(Model model) {
        model.addAttribute("countProjects", projectsService.getCountProjects());
        model.addAttribute("countEmployees", employeesService.getCountEmployees());
        return "mainPage/index";
    }
}
