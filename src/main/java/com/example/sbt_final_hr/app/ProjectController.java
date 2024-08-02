package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.*;

import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        return "project/readAllProjects";
    }


    @GetMapping("/readAssignedProjects")
    public String readAssignedProjects(HttpSession httpSession) {
        httpSession.setAttribute("projects", projectsService.getAssignedProjects());
        return "project/readAssignedProjects"; // 가상의 주소
    }

    //모달 띄우기 전에 여기에 요청해서 어트리뷰트 가져가는 메서드
    @GetMapping("/getInfoByProjectID2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInfoByProjectID2(@RequestParam("id") Long id, HttpSession session) {
        List<ProjectRequirements> projectRequirements = projectRequirementsService.getRequirementsByProjectId(id);
        List<EmployeesProjectsRequest> employeesProjectsRequests =
                employeesProjectsService.getEmployeesProjectByProjectId(id).stream().map(ep -> {
//                    System.out.println(ep.getEmployee().getName());
//                    System.out.println(ep.getEmployee().getEmployeeId());
                    EmployeesProjectsRequest request = new EmployeesProjectsRequest();
                    request.fromEntity(ep);
                    return request;
                }).toList();

        List<Long> eprID = new ArrayList<Long>();

        for (EmployeesProjectsRequest req : employeesProjectsRequests) {
            if (req.getEmployeeName() != null) {
                System.out.print("Employee ID: " + req.getEmployee().getEmployeeId());
                System.out.println(" Employee: " + req.getEmployeeName());

                eprID.add(req.getEmployee().getEmployeeId());
            } else {
                System.out.println("Employee is null or name is null");
            }
//            System.out.println("Project Duration: " + req.getProjectDuration());
//            System.out.println("----");
        }
        // 순환 참조 문제 해결을 위해, dto 에 추가 컬럼 만들고, employee 속성에는 @JsonIgnore 처리
//        System.out.println("pr: " + projectRequirements);
//        System.out.println("ep: " + employeesProjectsRequests);
        // 배정 관리 페이지에서도 db에 요청 없이도 쓰기 위해서 세션
        session.setAttribute("projectId", id);
        // 해당 프로젝트의 요구사항
        session.setAttribute("projectRequirements", projectRequirements);
        // 해당 프로젝트에 해당하는 사원-프로젝트 테이블 행
        session.setAttribute("employeesProjects", employeesProjectsRequests);

        session.setAttribute("eprID", eprID);

        Map<String, Object> response = new HashMap<>();
        response.put("projectId", id);
        response.put("projectRequirements", projectRequirements);
        response.put("employeesProjects", employeesProjectsRequests);
        response.put("eprID",eprID);

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

        return "redirect:/readAllProjects";
    }

    @GetMapping("/deleteProject")
    public String deleteProject(@RequestParam Map<String, String> payload, Model model) {
        long id = Long.parseLong(payload.get("id"));
        projectRequirementsService.deleteByProjectId(id);
        projectsService.deleteProject(id);
        System.out.println("삭제 성공");
        return "redirect:/readAllProjects";
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
//    @PostMapping("/completeProject")
//    public ResponseEntity<String> completeProject(@RequestBody Map<String, Long> request) {
//        Long projectId = request.get("projectId");
//        System.out.println(projectId);
//        //해당 메서드를 실행하기 위해 필요한 객체들 다 가지고 왔나 콘솔로 찍어봐야해
//
//        // 프로젝트 상태 업데이트 : 프로젝트 완료 누르면 project 테이블에서 status 를 1 => 2로 바꾸기
//        projectsService.updateStatusTo(projectId, 2);
//        // 프로젝트에 참여한 사원들의 스킬 경력 업데이트
//        //employeesProjectsService.updateEmployeeSkillsForCompletedProject(projectId); ???
//        // 프로젝트에 참여한 사원의 별점 업데이트 (employees_project table)
//        //employeesProjectsService.updateEmployeeStarpointForCompletedProject(projectId); ???
//        // 프로젝트에 참여한 사원의 별점 평균 업데이트 (employees table)
//        // 프로젝트 참여한 사원의...employees 테이블 : LAST_PROJECT_END_DATE의 값은, CURRENT_PROJECT_END_DATE의 값으로 업데이트 되게 되고, 그 다음에 CURRENT_PROJECT_END_DATE는 NULL로 바뀌게 된다.
//LAST_PROJECT_END_DATE = CURRENT_PROJECT_END_DATE;
//CURRENT_PROJECT_END_DATE = null;
//
//        return ResponseEntity.ok("Project status updated to completed");
//    }

    @PostMapping("/completeProject")
    public ResponseEntity<?> completeProject(@RequestBody Map<String, Object> payload) {
        Long projectId = ((Number) payload.get("projectId")).longValue();
        List<Map<String, Object>> starPoints = (List<Map<String, Object>>) payload.get("starPoints");
        //담아야 할거 : 안건 참여 사원id(충족), 안건-사원 별점(충족), 그 안건에서 그 사원이 사용한 프로그래밍 언어, projectDuration.

        // 프로젝트 ID 출력
        System.out.println("Project ID: " + projectId);
        //status 1=>2 작동은 되는데 숫자 바꾸기 귀찮아서 밑에 메서드 일단 주석 써서 일단 막아둠
        //projectsService.updateStatusTo(projectId, 2);
        // starPoints 리스트를 처리하는 로직 추가
        for (Map<String, Object> starPoint : starPoints) {
            Long employeeId = ((Number) starPoint.get("employeeId")).longValue();
            Double point = ((Number) starPoint.get("starPoint")).doubleValue();
            Long skillId = ((Number) starPoint.get("skillId")).longValue();

            // employeeId와 point를 이용하여 필요한 로직 처리

            // employeeId와 point 출력
            System.out.println("Employee ID: " + employeeId + ", Star Point: " + point  + ", Skill ID: " + skillId);
        }
        //starPoint 객체에 project_Duration도 넣자..... 8월 2일 20:32 주말ㅇ ㅔ ㄱㄱ

        // 프로젝트 완료 처리 로직
        return ResponseEntity.ok().body("Project completed successfully");
    }
}
