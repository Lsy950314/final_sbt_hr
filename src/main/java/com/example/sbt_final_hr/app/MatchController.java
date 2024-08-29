package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MatchController {
    private final MatchService matchService;
    private final ProjectsService projectsService;
    private final ProjectRequirementsService projectRequirementsService;

    public MatchController(MatchService matchService, ProjectsService projectsService, EmployeesService employeesService, ProjectRequirementsService projectRequirementsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService1) {
        this.matchService = matchService;
        this.projectsService = projectsService;
        this.projectRequirementsService = projectRequirementsService;
    }

    @GetMapping("/matchManagement")
    public String matchManagement(HttpSession session, Model model, SessionStatus sessionStatus) {
        Long projectId = (Long) session.getAttribute("projectId");
        List<ProjectRequirements> projectRequirements = projectRequirementsService.getRequirementsByProjectId(projectId);
        if (projectId == null || projectRequirements == null) {
            return "redirect:/readAllProjects";
        }

        Projects projects = projectsService.getProjectById(projectId);

        model.addAttribute("Projects", projects);
        model.addAttribute("projectRequirements", projectRequirements);
        model.addAttribute("currentDate", LocalDate.now());

        Map<Employees, Integer> filteredEmployeesTransitTimes = matchService.filterEmployeesForProject(projects);
        List<Map.Entry<Employees, Integer>> employeeEntries = new ArrayList<>(filteredEmployeesTransitTimes.entrySet());

        model.addAttribute("employeeEntries", employeeEntries);

        return "match/matchManagement";
    }

    @GetMapping("/matchEmployeeProject")
    public ResponseEntity<Void> matchEmployeeProject(@RequestParam Map<String, String> payload, HttpSession httpSession) {
        Long projectId = Long.parseLong(payload.get("projectId"));
        Long employeeId = Long.parseLong(payload.get("employeeId"));
        Long projectRequirementsId = Long.parseLong(payload.get("projectRequirementsId"));

        if (matchService.matchEmployeeToProject(projectId, employeeId, projectRequirementsId)) {
            httpSession.removeAttribute("projects");
            httpSession.removeAttribute("projectsType");
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/matchCancel")
    public ResponseEntity<String> matchCancelEmployeeProject(@RequestParam Map<String, String> payload, HttpSession httpSession) {
        Long projectId = Long.parseLong(payload.get("projectId"));
        Long employeeId = Long.parseLong(payload.get("employeeId"));
        Long projectRequirementsId = Long.parseLong(payload.get("projectRequirementsId"));

        if (matchService.matchCancel(projectId, employeeId, projectRequirementsId)) {
            httpSession.removeAttribute("projects");
            httpSession.removeAttribute("projectsType");
            return ResponseEntity.ok("キャンセル成功");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("キャンセル失敗");
        }

    }

}
