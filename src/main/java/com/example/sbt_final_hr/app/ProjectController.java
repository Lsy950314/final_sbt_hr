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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

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
    private final EmployeesService employeesService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public ProjectController(ProjectsService projectsService, ProjectTypesService projectTypesService, ProjectRequirementsService projectRequirementsService, SkillsService skillsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService) {
        this.projectsService = projectsService;
        this.projectTypesService = projectTypesService;
        this.projectRequirementsService = projectRequirementsService;
        this.skillsService = skillsService;
        this.employeesProjectsService = employeesProjectsService;
        this.employeesService = employeesService;
    }

    @GetMapping("/readAllProjects")
    public String readAllProjects(Model model) {
        model.addAttribute("projects", projectsService.getAllProjects());
        return "project/readAllProjects"; // 가상의 주소
    }

    // 모달 띄우기 전에 여기에 요청해서 어트리뷰트 가져가는 메서드
    @GetMapping("/getInfoByProjectID")
    public void getInfoByProjectID(@RequestParam("id") Long id, HttpSession session) {
        List<ProjectRequirements> projectRequirements = projectRequirementsService.getRequirementsByProjectId(id);
        List<EmployeesProjects> employeesProjects = employeesProjectsService.getEmployeesProjectByProjectId(id);
        List<Employees> employees = employeesProjectsService.getEmployeesByProjectId(id);

        session.setAttribute("projectId", id);
        session.setAttribute("projectRequirements", projectRequirements);
        session.setAttribute("employeesProjects", employeesProjects);
        session.setAttribute("employees", employees);
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

        model.addAttribute("projectId", projectId);
        model.addAttribute("projectRequirements", projectRequirements);
        model.addAttribute("employeesProjects", employeesProjects);
        model.addAttribute("employees", employees);



        return "project/matchManagement";
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
        projectsService.updateProject(projectsRequest);

        Projects project = projectsRequest.toEntity(projectTypesService.getProjectTypeById(projectsRequest.getProjectTypeId()));
        if (projectsRequest.getProjectRequirements() != null) {
            for (ProjectRequirementsRequest requirementsRequest : projectsRequest.getProjectRequirements()) {
                if (requirementsRequest.getId() != null) {
                    projectRequirementsService.updateProjectRequirements(requirementsRequest, project);
                } else {
                    ProjectRequirements projectRequirements = requirementsRequest.toEntity(project);
                    projectRequirementsService.createProjectRequirements(projectRequirements);
                }
            }
        }

        return "redirect:/updateProject?id=" + projectsRequest.getProjectId();
    }

    @GetMapping("/deleteProject")
    public String deleteProject(@RequestParam Map<String, String> payload, Model model) {
        long id = Long.parseLong(payload.get("id"));
        projectRequirementsService.deleteByProjectId(id);
        projectsService.deleteProject(id);
        System.out.println("삭제 성공");
        return "redirect:/createProject";
    }

}
