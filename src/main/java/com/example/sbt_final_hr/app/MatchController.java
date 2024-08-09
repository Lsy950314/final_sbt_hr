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

    public MatchController(MatchService matchService, ProjectsService projectsService, EmployeesService employeesService, ProjectRequirementsService projectRequirementsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService1) {
        this.matchService = matchService;
        this.projectsService = projectsService;
    }

    @GetMapping("/check")
    public String check() {
        return "employees/check";
    }


    @GetMapping("/matchManagement")
    public String matchManagement(HttpSession session, Model model, SessionStatus sessionStatus) {
        Long projectId = (Long) session.getAttribute("projectId");
        List<ProjectRequirements> projectRequirements = (List<ProjectRequirements>) session.getAttribute("projectRequirements");
        List<EmployeesProjects> employeesProjects = (List<EmployeesProjects>) session.getAttribute("employeesProjects");
        if (projectId == null || projectRequirements == null || employeesProjects == null) {
            // 모달창을 띄우고 시간이 지난 후에 배정 관리 페이지 버튼을 누르는 바람에
            // 세션이 만료된 경우, 에러 페이지로 리다이렉트하거나 적절한 처리를 수행
            return "redirect:/readAllProjects";  // 리스트 페이지로 돌려보내기
        }

//      session.removeAttribute("projectId");
//      session.removeAttribute("projectRequirements");
//      session.removeAttribute("employeesProjects");

        Projects projects = projectsService.getProjectById(projectId);
//      System.out.println(projects);
//      System.out.println(projectRequirements);
//      System.out.println(employeesProjects);

        model.addAttribute("Projects", projects);

        // 해당 프로젝트의 요구사항
        model.addAttribute("projectRequirements", projectRequirements);

        // 해당 프로젝트에 해당하는 사원-프로젝트 테이블 행
        model.addAttribute("employeesProjects", employeesProjects);

        // 현재 시간 정보와 프로젝트 종료일을 통해 대기 기간을 얻어내기 위해서
        model.addAttribute("currentDate", LocalDate.now());

        Map<Employees, Integer> filteredEmployeesTransitTimes = matchService.filterEmployeesForProject(projects);
        List<Map.Entry<Employees, Integer>> employeeEntries = new ArrayList<>(filteredEmployeesTransitTimes.entrySet());

        model.addAttribute("employeeEntries", employeeEntries);

        return "match/matchManagement";
    }

    @GetMapping("/matchEmployeeProject")
    public ResponseEntity<Void> matchEmployeeProject(@RequestParam Map<String, String> payload) {
        Long projectId = Long.parseLong(payload.get("projectId"));
        Long employeeId = Long.parseLong(payload.get("employeeId"));
        Long projectRequirementsId = Long.parseLong(payload.get("projectRequirementsId"));

//      System.out.println(projectId);
//      System.out.println(employeeId);
//      System.out.println(projectRequirementsId);

        matchService.matchEmployeeToProject(projectId, employeeId, projectRequirementsId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/matchCancel")
    public ResponseEntity<String> matchCancelEmployeeProject(@RequestParam Map<String, String> payload) {
        Long projectId = Long.parseLong(payload.get("projectId"));
        Long employeeId = Long.parseLong(payload.get("employeeId"));
        Long projectRequirementsId = Long.parseLong(payload.get("projectRequirementsId"));

        if (matchService.matchCancel(projectId, employeeId, projectRequirementsId)) {
            return ResponseEntity.ok("취소 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("취소 실패");
        }

    }

}
