package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public ProjectController(ProjectsService projectsService, ProjectTypesService projectTypesService, ProjectRequirementsService projectRequirementsService, SkillsService skillsService, EmployeesProjectsService employeesProjectsService) {
        this.projectsService = projectsService;
        this.projectTypesService = projectTypesService;
        this.projectRequirementsService = projectRequirementsService;
        this.skillsService = skillsService;
        this.employeesProjectsService = employeesProjectsService;
    }

    @GetMapping("/readAllProjects")
    public String readAllProjects(Model model) {
        model.addAttribute("projects", projectsService.getAllProjects());

//        model.addAttribute("employeesProjects", employeesProjectsService.getAllEmployeesProjects());

        // 리스트에서 항목을 선택하면 디테일 페이지를 모달로 띄운다?
        // 거기서 projects 가 가진 정보 외에도 필요한 것.
        // 참여 인원, 필요 인원
        // 현재 참여중인 인원들(진행 중인 프로젝트) or 배정 완료된 인원들(시작 전 프로젝트): 구하는 로직 동일할듯?
        // 전체 사원-안건 테이블 전체 정보를 리스트 띄울 때 가져가지 말고,
        // 특정 프로젝트를 선택했을 때 그에 해당하는 사원-안건 테이블을 프로젝트pk로 조회해서,
        // 얻어낸 사원pk를 통해 사원엔티티 얻어서 모달에 보여줄 때 사용하기
        // -> 메서드 따로 설정 ("/getEmployeeProjectsByProjectID")
        // PROJECT_REQUIREMENTS 테이블을 프로젝트pk로 조회해서, 얻어낸 List<ProjectRequirements> 들을 표시해주기

        return "project/readAllProjects"; // 가상의 주소
    }

    // 모달 띄우기 전에 여기에 요청해서 어트리뷰트 가져가는 메서드
    @GetMapping("/getEmployeeProjectsByProjectID")
    public void getEmployeeProjectsByProjectID(@RequestParam Map<String, String> payload, Model model) {
        Long id = Long.parseLong(payload.get("id"));
        // 해당 프로젝트에 해당하는 사원-안건 엔티티만 얻어내고
        List<EmployeesProjects> employeesProjects = employeesProjectsService.getEmployeesProjectByProjectId(id);


        model.addAttribute("employeesProjects", employeesProjects);


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
