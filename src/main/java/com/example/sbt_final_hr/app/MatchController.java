package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.EmployeesService;
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
    private final EmployeesService employeesService;

    public MatchController(MatchService matchService, ProjectsService projectsService, EmployeesService employeesService) {
        this.matchService = matchService;
        this.projectsService = projectsService;
        this.employeesService = employeesService;
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
        // 세션 무효화
        session.invalidate();

        // 필요한 경우, 특정 속성만 제거
        // session.removeAttribute("projectId");
        // session.removeAttribute("projectRequirements");
        // session.removeAttribute("employeesProjects");
        // session.removeAttribute("employees");
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

        // 스킬스택 요구 조건을 만족한 사원들
//           List<Employees> filteredEmployees= matchService.testFilter(projects);
        // 시간이 너무 걸려서 이 방법 말고 직접 쿼리문 작성해서 db 에서 처리시키는 안 채택

        List<Employees> filteredEmployees = matchService.filterEmployeesForProject(projects);

        model.addAttribute("filteredEmployees", filteredEmployees);

        return "match/matchManagement";
    }
}
