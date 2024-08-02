package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProjectController {
    private final ProjectsService projectsService;
    private final ProjectTypesService projectTypesService;
    private final ProjectRequirementsService projectRequirementsService;
    private final SkillsService skillsService;
    private final EmployeesProjectsService employeesProjectsService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public ProjectController(ProjectsService projectsService, ProjectTypesService projectTypesService, ProjectRequirementsService projectRequirementsService, SkillsService skillsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService) {
        this.projectsService = projectsService;
        this.projectTypesService = projectTypesService;
        this.projectRequirementsService = projectRequirementsService;
        this.skillsService = skillsService;
        this.employeesProjectsService = employeesProjectsService;
    }

    @GetMapping("/readAllProjects")
    public String readAllProjects(HttpSession httpSession) {
        httpSession.setAttribute("projects", projectsService.getAllProjects());
        return "project/readAllProjects"; // 가상의 주소
    }
    //projects 테이블에서 status가 1인 튜플들(-1:미배정, 1:배정)만 가져오는 리스트 페이지
//    @GetMapping("/readAssignedProjects")
//    public String readAssignedProjects(HttpSession httpSession) {
//        httpSession.setAttribute("projects", projectsService.getAssignedProjects());
//        return "project/readAssignedProjects"; // 가상의 주소
//    }
    //완료 버튼 누르면 그 프로젝트에 참여한 인원들 리스트 가져오기
//    @GetMapping("/readAssignedProjects")
//    public String readAssignedProjects(HttpSession httpSession, @RequestParam("id") Long id) {
//        List<Employees> employees = employeesProjectsService.getEmployeesByProjectId(id);
//        httpSession.setAttribute("projects", projectsService.getAssignedProjects());
//        httpSession.setAttribute("assignedemployees", employees);
//        return "project/readAssignedProjects"; // 가상의 주소
//    }

    @GetMapping("/readAssignedProjects")
    public String readAssignedProjects(HttpSession httpSession) {
        httpSession.setAttribute("projects", projectsService.getAssignedProjects());
        return "project/readAssignedProjects"; // 가상의 주소
    }

    // 모달 띄우기 전에 여기에 요청해서 어트리뷰트 가져가는 메서드
    @GetMapping("/getInfoByProjectID")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInfoByProjectID(@RequestParam("id") Long id, HttpSession session) {
        List<ProjectRequirements> projectRequirements = projectRequirementsService.getRequirementsByProjectId(id);
        List<EmployeesProjects> employeesProjects = employeesProjectsService.getEmployeesProjectByProjectId(id);
        List<Employees> employees = employeesProjectsService.getEmployeesByProjectId(id);

        // 배정 관리 페이지에서도 db에 요청 없이도 쓰기 위해서 세션 써보는 중
        session.setAttribute("projectId", id);

        // 해당 프로젝트의 요구사항
        session.setAttribute("projectRequirements", projectRequirements);

        // 해당 프로젝트에 해당하는 사원-프로젝트 테이블 행
        session.setAttribute("employeesProjects", employeesProjects);

        // 해당 프로젝트에 참여중인 사원들
        session.setAttribute("employees", employees);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", id);
        response.put("projectRequirements", projectRequirements);
        response.put("employeesProjects", employeesProjects);
        response.put("employees", employees);

        return ResponseEntity.ok(response);
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
        Projects project = projectsService.updateProject(projectsRequest);
        projectRequirementsService.deleteByProjectId(project.getProjectId());

        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                projectRequirementsService.createProjectRequirements(projectRequirements);
            }
        }

        return "redirect:/updateProject?id=" + project.getProjectId();
    }

    @GetMapping("/deleteProject")
    public String deleteProject(@RequestParam Map<String, String> payload, Model model) {
        long id = Long.parseLong(payload.get("id"));
        projectRequirementsService.deleteByProjectId(id);
        projectsService.deleteProject(id);
        System.out.println("삭제 성공");
        return "redirect:/createProject";
    }

//8월 1일 16:47 프로젝트 완료 눌렀을 때 여러 테이블 crud 처리하는 컨트롤러 코드
//@PostMapping("/completeProject")
//public ResponseEntity<String> completeProject(@RequestBody EmployeesProjectsRequest completeProjectRequest) {
//    System.out.println("Completing project ID: " + completeProjectRequest.getProject().getProjectId());
//
//    // 여기에서 프로젝트 완료 로직을 구현하세요.
//    // 예: 프로젝트 상태 변경, 직원들 업데이트 등.
//
//    return ResponseEntity.ok("Project completed successfully");
//}

//8월 1일 17:44
    @PostMapping("/completeProject")
    public ResponseEntity<String> completeProject(@RequestBody Map<String, Long> request) {
        Long projectId = request.get("projectId");
        // 프로젝트 상태 업데이트 : 프로젝트 완료 누르면 project 테이블에서 status 를 1 + 2로 바꾸기
        projectsService.updateProjectStatus(projectId, 2);
        // 프로젝트에 참여한 사원들의 스킬 경력 업데이트
        //employeesProjectsService.updateEmployeeSkillsForCompletedProject(projectId); ???
        // 프로젝트에 참여한 사원의 별점 업데이트 (employees_project table)
        //employeesProjectsService.updateEmployeeStarpointForCompletedProject(projectId); ???
        // 프로젝트에 참여한 사원의 별점 평균 업데이트 (employees table)

        return ResponseEntity.ok("Project status updated to completed");
    }


}
