package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.MatchService;
import com.example.sbt_final_hr.domain.service.ProjectsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
public class MatchController {
    private final MatchService matchService;
    private final ProjectsService projectsService;

    public MatchController(MatchService matchService, ProjectsService projectsService) {
        this.matchService = matchService;
        this.projectsService = projectsService;
    }

    @GetMapping("/matchManagement")
    public String matchManagement(HttpSession session, Model model, SessionStatus sessionStatus) {
        Long projectId = (Long) session.getAttribute("projectId");
        List<ProjectRequirements> projectRequirements = (List<ProjectRequirements>) session.getAttribute("projectRequirements");
        List<EmployeesProjects> employeesProjects = (List<EmployeesProjects>) session.getAttribute("employeesProjects");
        List<Employees> employees = (List<Employees>) session.getAttribute("employees");
        if (projectId == null || projectRequirements == null || employeesProjects == null || employees == null) {
            // 모달창을 띄우고 시간이 지난 후에 배정 관리 페이지 버튼을 누르는 바람에
            // 세션이 만료된 경우, 에러 페이지로 리다이렉트하거나 적절한 처리를 수행
            return "redirect:/readAllProjects";  // 리스트 페이지로 돌려보내기
        }
        sessionStatus.setComplete();

        Projects projects = projectsService.getProjectById(projectId);

        model.addAttribute("projectId", projectId);
        model.addAttribute("projectRequirements", projectRequirements);
        model.addAttribute("employeesProjects", employeesProjects);
        model.addAttribute("employees", employees);

        return "project/matchManagement.html";
    }
}
