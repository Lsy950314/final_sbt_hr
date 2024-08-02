package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
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

        session.removeAttribute("projectId");
        session.removeAttribute("projectRequirements");
        session.removeAttribute("employeesProjects");

        Projects projects = projectsService.getProjectById(projectId);
//        System.out.println(projects);
//        System.out.println(projectRequirements);
//        System.out.println(employeesProjects);

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

 // 사원 비교를 위해 만든 컨트롤러
 @PostMapping("/compareEmployee")
 public String compareEmployee(@RequestParam("employeeId") Long employeeId, Model model, HttpSession session) {
     // 선택된 사원의 ID로 사원 정보를 조회합니다.
     Employees selectedEmployee = employeesService.findEmployeeById(employeeId);

     if (selectedEmployee != null) {
         model.addAttribute("selectedEmployee", selectedEmployee);
     }

     // matchManagement에서 사용되는 기존 데이터도 함께 전달해야 합니다.
     Long projectId = (Long) session.getAttribute("projectId");
     List<ProjectRequirements> projectRequirements = (List<ProjectRequirements>) session.getAttribute("projectRequirements");
     List<EmployeesProjects> employeesProjects = (List<EmployeesProjects>) session.getAttribute("employeesProjects");

     if (projectId != null && projectRequirements != null && employeesProjects != null) {
         Projects projects = projectsService.getProjectById(projectId);
         model.addAttribute("Projects", projects);
         model.addAttribute("projectRequirements", projectRequirements);
         model.addAttribute("employeesProjects", employeesProjects);
         model.addAttribute("currentDate", LocalDate.now());

         Map<Employees, Integer> filteredEmployeesTransitTimes = matchService.filterEmployeesForProject(projects);
         List<Map.Entry<Employees, Integer>> employeeEntries = new ArrayList<>(filteredEmployeesTransitTimes.entrySet());

         model.addAttribute("employeeEntries", employeeEntries);
     }

     return "match/matchManagement"; // 동일한 뷰로 돌아감
 }





    @GetMapping("/matchEmployeeProject")
    public ResponseEntity<Void> matchEmployeeProject(@RequestParam Map<String, String> payload) {
        Long projectId = Long.parseLong(payload.get("projectId"));
        Long employeeId = Long.parseLong(payload.get("employeeId"));
        Long projectRequirementsId = Long.parseLong(payload.get("projectRequirementsId"));

//        System.out.println(projectId);
//        System.out.println(employeeId);
//        System.out.println(projectRequirementsId);

        if (projectRequirementsService.updateFulfilledCount(projectRequirementsId)) {
            if (employeesProjectsService.insertEmployeesProjects(employeeId, projectId, projectRequirementsId)) {
                employeesService.updateEndDates(employeeId, projectId);
            }
        }
        
        // 모든 요구인원이 충족됐다면 projects 의 status 를 1로 바꿔주는 로직
        if (projectRequirementsService.checkFulfilledCount(projectId)) {
            projectsService.updateStatus(projectId);
        }

        return ResponseEntity.ok().build();
    }

    // 배정된 사람을 취소하는 로직?
    // 배정 로직을 반대로
    // 충족인원 -1
    // 생성했던 사원-프로젝트 테이블 행 삭제
    // 최근(직전) 프로젝트 종료일을 null 로 바꾸면서, 복구 정보 기록용 속성을 하나 만들기
    // 배정 취소시에 해당 복구 정보가 기록된 속성을 이용해서 값을 얻어내기
}
