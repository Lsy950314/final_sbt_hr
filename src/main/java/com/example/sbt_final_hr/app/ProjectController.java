package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.*;

import com.example.sbt_final_hr.domain.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
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
    private final EmployeesSkillService employeesSkillService;
    private final EmployeesService employeesService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public ProjectController(ProjectsService projectsService, ProjectTypesService projectTypesService, ProjectRequirementsService projectRequirementsService, SkillsService skillsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService, EmployeesSkillService employeesSkillService, EmployeesService employeesService1) {
        this.projectsService = projectsService;
        this.projectTypesService = projectTypesService;
        this.projectRequirementsService = projectRequirementsService;
        this.skillsService = skillsService;
        this.employeesProjectsService = employeesProjectsService;
        this.employeesSkillService = employeesSkillService;
        this.employeesService = employeesService1;
    }

    @Value("${project.imminent-start-days}")
    private int imminentStartDays;

    @Value("${project.imminent-end-days}")
    private int imminentEndDays;

    @GetMapping("/readAllProjects")
    public String readAllProjects(HttpSession httpSession, @RequestParam(value = "employeeId", required = false) Long employeeId,
                                  @RequestParam(value = "filterStatus", required = false) Integer filterStatus,
                                  @RequestParam(value = "sortBy", required = false) String sortBy,
                                  Model model) {
//      long startTime = System.currentTimeMillis();

//        System.out.println(filterStatus);
//        System.out.println(sortBy);

        List<ProjectsRequest> projects;
        String projectsType = (String) httpSession.getAttribute("projectsType");
        System.out.println(projectsType);

        if (employeeId != null) {
            projects = projectsService.getProjectByEmployee(employeeId);
            httpSession.setAttribute("projects", projects);
            httpSession.setAttribute("projectsType", "employee");
        } else {
            if (projectsType == null || !projectsType.equals("all")) {
                projects = projectsService.getAllProjectsSummary();
                httpSession.setAttribute("projects", projects);
                httpSession.setAttribute("projectsType", "all");
            }
        }

        model.addAttribute("imminentStartDays", imminentStartDays);
        model.addAttribute("imminentEndDays", imminentEndDays);

        // 셀렉트 옵션을 위해
        model.addAttribute("filterStatus", filterStatus);
        model.addAttribute("sortBy", sortBy);

//        long endTime = System.currentTimeMillis();
//        System.out.println("took: " + (endTime - startTime) + " milliseconds");

        return "project/readAllProjects";
    }

    @GetMapping("/readAssignedProjects")
    public String readAssignedProjects(HttpSession httpSession) {
        httpSession.setAttribute("projects", projectsService.getAssignedProjects());
        return "project/readAssignedProjects"; // 가상의 주소
    }

    //모달 띄우기 전에 여기에 요청해서 어트리뷰트 가져가는 메서드
    @GetMapping("/getInfoByProjectID")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInfoByProjectID(@RequestParam("id") Long id, HttpSession session) {
        List<ProjectRequirements> projectRequirements = projectRequirementsService.getRequirementsByProjectId(id);
        List<EmployeesProjectsRequest> employeesProjectsRequests =
                employeesProjectsService.getEmployeesProjectByProjectId(id).stream().map(ep -> {
//                    System.out.println(ep.getEmployee().getName());
//                    System.out.println(ep.getEmployee().getEmployeeId());
                    EmployeesProjectsRequest request = new EmployeesProjectsRequest();
                    request.fromEntity(ep);
                    return request;
                }).toList();

        List<Long> epID = new ArrayList<Long>();
        for (EmployeesProjectsRequest req : employeesProjectsRequests) {
            if (req.getEmployeeName() != null) {
                System.out.print("Employee ID: " + req.getEmployee().getEmployeeId());
                System.out.println(" Employee: " + req.getEmployeeName());

                epID.add(req.getEmployee().getEmployeeId());
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

        session.setAttribute("epID", epID);

        Map<String, Object> response = new HashMap<>();
        response.put("project", projectsService.getProjectById(id));
        response.put("projectId", id);
        response.put("projectRequirements", projectRequirements);
        response.put("employeesProjects", employeesProjectsRequests);
        response.put("epID", epID);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getProjectRequirements")
    @ResponseBody
    public List<ProjectRequirements> getProjectRequirements(@RequestParam Long projectId) {
        return projectRequirementsService.findByProjectId(projectId);
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
    public String createProject(@ModelAttribute ProjectsRequest projectsRequest, HttpSession httpSession) {
        Projects project = projectsService.createProject(projectsRequest);

        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                projectRequirementsService.createProjectRequirements(projectRequirements);
            }
        }

        httpSession.removeAttribute("projects");
        httpSession.removeAttribute("projectsType");
        return "redirect:/readAllProjects";
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
    public String updateProject(@ModelAttribute("projectsRequest") ProjectsRequest projectsRequest, HttpSession httpSession) {
        Projects project = projectsService.updateProject(projectsRequest);
        projectRequirementsService.deleteByProjectId(project.getProjectId());

        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                projectRequirementsService.createProjectRequirements(projectRequirements);
            }
        }

        httpSession.removeAttribute("projects");
        httpSession.removeAttribute("projectsType");
        return "redirect:/readAllProjects";
    }

    @GetMapping("/deleteProject")
    public String deleteProject(@RequestParam Map<String, String> payload, Model model, HttpSession httpSession) {
        long id = Long.parseLong(payload.get("id"));
        projectRequirementsService.deleteByProjectId(id);
        projectsService.deleteProject(id);
        System.out.println("삭제 성공");

        httpSession.removeAttribute("projects");
        httpSession.removeAttribute("projectsType");
        return "redirect:/readAllProjects";
    }

    @PostMapping("/completeProject")
    public ResponseEntity<?> completeProject(@RequestBody Map<String, Object> payload, HttpSession httpSession) {
        Long projectId = ((Number) payload.get("projectId")).longValue();
        List<Map<String, Object>> projectParticipantsInfos = (List<Map<String, Object>>) payload.get("projectParticipantsInfos");
        System.out.println("Project ID: " + projectId);
        for (Map<String, Object> participantInfo : projectParticipantsInfos) {
            long employeeId = ((Number) participantInfo.get("employeeId")).longValue();
            double point = ((Number) participantInfo.get("starPoint")).doubleValue();
            long skillId = ((Number) participantInfo.get("skillId")).longValue();
            double projectDuration = ((Number) participantInfo.get("projectDuration")).doubleValue();
            System.out.println("Employee ID: " + employeeId + ", Star Point: " + point + ", Skill ID: " + skillId + ", projectDuration: " + projectDuration);

            // 프로젝트에 참여한 사원의 별점 업데이트 (employees_project table)
            employeesProjectsService.updateStarPointOfProjectParticipants(projectId, projectParticipantsInfos);
            // 프로젝트에 참여한 사원들의 스킬 경력 업데이트(employees_skill table)
            employeesSkillService.updateSkillCareerOfProjectParticipants(projectParticipantsInfos);
            // 프로젝트에 참여한 사원의 별점 평균 업데이트 (employees table)
            employeesService.updateStarPointAverageOfProjectParticipants(employeeId);
            //LAST_PROJECT_END_DATE를 CURRENT_PROJECT_END_DATE의 값을 넣고, CURRENT_PROJECT_END_DATE의의 값을 null로
            employeesService.updateProjectEndDateOfProjectParticipants(employeeId);
            //프로젝트에 참여한 사원의 allocation을 1 => -1 로 업데이트(employees table)
            employeesService.updateAllocationTo(employeeId, -1);
        }
        // 프로젝트의 status를 1 => 2로 변경
        projectsService.updateStatusTo(projectId, 2);
        httpSession.removeAttribute("projects");
        httpSession.removeAttribute("projectsType");
        return ResponseEntity.ok().body("プロジェクトが完了処理されました");
    }

}
