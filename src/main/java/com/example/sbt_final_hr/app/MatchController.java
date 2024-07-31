package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MatchController {
    private final MatchService matchService;
    private final ProjectsService projectsService;
    private final EmployeesService employeesService;
    private final ProjectRequirementsService projectRequirementsService;
    private final EmployeesProjectsService employeesProjectsService;


    public MatchController(MatchService matchService, ProjectsService projectsService, EmployeesService employeesService, ProjectRequirementsService projectRequirementsService, EmployeesProjectsService employeesProjectsService) {
        this.matchService = matchService;
        this.projectsService = projectsService;
        this.employeesService = employeesService;
        this.projectRequirementsService = projectRequirementsService;
        this.employeesProjectsService = employeesProjectsService;
    }

    @GetMapping("/check")
    public String check(){
        return "employees/check";
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

//        session.removeAttribute("projectId");
//        session.removeAttribute("projectRequirements");
//        session.removeAttribute("employeesProjects");
//        session.removeAttribute("employees");

        Projects projects = projectsService.getProjectById(projectId);

        System.out.println(projects);
        System.out.println(projectRequirements);
        System.out.println(employeesProjects);
        System.out.println(employees);

        model.addAttribute("Projects", projects);

        // 해당 프로젝트의 요구사항
        model.addAttribute("projectRequirements", projectRequirements);

        // 해당 프로젝트에 해당하는 사원-프로젝트 테이블 행
        model.addAttribute("employeesProjects", employeesProjects);

        // 해당 프로젝트에 참여중인 사원들
        model.addAttribute("employees", employees);

        // 현재 시간 정보와 프로젝트 종료일을 통해 대기 기간을 얻어내기 위해서
        model.addAttribute("currentDate", LocalDate.now());

        Map<Employees, Integer> filteredEmployeesTransitTimes = matchService.filterEmployeesForProject(projects);
        List<Map.Entry<Employees, Integer>> employeeEntries = new ArrayList<>(filteredEmployeesTransitTimes.entrySet());

        model.addAttribute("employeeEntries", employeeEntries);

        return "match/matchManagement";
    }

    @GetMapping("/matchEmployeeProject")
    public void matchEmployeeProject(@RequestParam Map<String, Long> payload){
        Long projectId = payload.get("projectId");
        Long employeeId = payload.get("employeeId");
        Long projectRequirementId = payload.get("projectRequirementId");

        employeesService.updateEndDates(employeeId, projectId);
        projectRequirementsService.updateFulfilledCount(projectRequirementId);
        employeesProjectsService.insertEmployeesProjects(employeeId, projectId, projectRequirementId);

    }
}
