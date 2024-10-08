package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.*;
import com.example.sbt_final_hr.domain.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employees")
public class EmployeesController {
    private final EmployeesProjectsService employeesProjectsService;
    private final EmployeesService employeesService;
    private final ProjectTypesService projectTypesService;
    private final SkillsService skillsService;
    private final EmployeesSkillService employeesSkillService;
    private final ProjectsService projectsService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Autowired
    public EmployeesController(EmployeesService employeesService, ProjectTypesService projectTypesService, SkillsService skillsService, EmployeesSkillService employeesSkillService, EmployeesProjectsService employeesProjectsService, EmployeesProjectsService employeesProjectsService1, ProjectsService projectsService) {
        this.employeesService = employeesService;
        this.projectTypesService = projectTypesService;
        this.skillsService = skillsService;
        this.employeesSkillService = employeesSkillService;
        this.employeesProjectsService = employeesProjectsService1;
        this.projectsService = projectsService;
    }

    @GetMapping("/newemployee")
    public String showCreateEmployeeFormWithPhoto(Model model) {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        model.addAttribute("employeesRequest", employeesRequest);
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "employees/createemployee";
    }

    @PostMapping("/createemployee")
    public String createEmployeeWithPhoto(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest,
                                          @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        String imagePath = employeesService.saveImage(imageFile);
        Employees employee = employeesService.save(employeesRequest.toEntity(imagePath));

        if (employeesRequest.getEmployeesSkillRequests() != null) {
            for (EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesSkillRequests()) {
                EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee);
                employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill);
            }
        }
        return "redirect:/employees";
    }



    @GetMapping
    public String listEmployeesSummary(HttpSession httpSession, @RequestParam(name = "name", required = false) String name,
                                       @RequestParam(name = "projectId", required = false) Long projectId, @RequestParam(name = "employeeId", required = false) Long employeeId,
                                       Model model) {

        if (projectId != null) {
            List<Employees> employees;
            employees = employeesService.findByProjectId(projectId);
            httpSession.setAttribute("employees", employees);
        } else if (name != null && !name.isEmpty()) {
            List<Employees> employees;
            employees = employeesService.findByName(name);
            httpSession.setAttribute("employees", employees);
        } else if (employeeId != null) {
            Employees employees;
            employees = employeesService.findByEmployeeId(employeeId);
            httpSession.setAttribute("employees", employees);
        } else {
            List<EmployeesRequest> employees;
            employees = employeesService.findAllEmployeesSummary();
            httpSession.setAttribute("employees", employees);
        }

        return "employees/employeeslist";
    }




    @PostMapping("/getModalData")
    public ResponseEntity<Map<String, Object>> getEmployeeModalData(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Optional<Employees> employees = employeesService.findById(id);
        List<EmployeesProjects> employeesProjects =  employeesProjectsService.findByEmployeeId(id);
        List<Long> projectIds = employeesProjects.stream()
                .map(ep -> ep.getProject().getProjectId())
                .collect(Collectors.toList());

        for (EmployeesProjects project : employeesProjects) {
            Long projectId = project.getProject().getProjectId();
            projectIds.add(projectId);
        }
        if (employees.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Employees employee = employees.get();
        List<EmployeesSkill> employeeSkills = employee.getSkills();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employee.getEmployeeId());
        response.put("name", employee.getName());
        response.put("address", employee.getAddress());
        response.put("lastProjectEndDate", employee.getLastProjectEndDate() != null ? employee.getLastProjectEndDate().format(formatter) : null);
        response.put("currentProjectEndDate", employee.getCurrentProjectEndDate() != null ? employee.getCurrentProjectEndDate().format(formatter) : null);
        response.put("contactNumber", employee.getContactNumber());
        response.put("hireDate", employee.getHireDate() != null ? employee.getHireDate().format(formatter) : null);
        response.put("preferredLanguage", employee.getSkill().getSkillName());
        response.put("preferredProjectType", employee.getProjectType().getProjectTypeName());
        response.put("latitude", employee.getLatitude());
        response.put("longitude", employee.getLongitude());
        response.put("image", employee.getImage());


        List<Map<String, Object>> skills = new ArrayList<>();
        for (EmployeesSkill skill : employeeSkills) {
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("skillName", skill.getSkill().getSkillName());
            skillInfo.put("skillCareer", skill.getSkillCareer());
            skills.add(skillInfo);
        }
        response.put("skills", skills);

        List<Projects> recentProjects = projectsService.findRecentProjectsByIds(projectIds);

        List<Map<String, Object>> projectInfos = new ArrayList<>();
        for (Projects project : recentProjects) {
            Map<String, Object> projectInfo = new HashMap<>();
            projectInfo.put("projectId", project.getProjectId());
            projectInfo.put("projectName", project.getProjectName());
            projectInfo.put("workLocation", project.getWorkLocation());
            projectInfo.put("clientCompany", project.getClientCompany());
            projectInfo.put("startDate", project.getStartDate());
            projectInfo.put("endDate", project.getEndDate());
            projectInfo.put("status", project.getStatus());
            projectInfo.put("latitude", project.getLatitude());
            projectInfo.put("longitude", project.getLongitude());
            projectInfo.put("contactPhone", project.getContactPhone());
            projectInfo.put("contactName", project.getContactName());
            projectInfo.put("registrationDate", project.getRegistrationDate() != null ? project.getRegistrationDate().format(DateTimeFormatter.ISO_DATE_TIME) : null);
            projectInfo.put("projectType", project.getProjectType().getProjectTypeName());
            projectInfo.put("totalProjectDurationInMonths", project.getTotalProjectDurationInMonths());
            projectInfos.add(projectInfo);
        }
        response.put("projects", projectInfos);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/edit/{id}")
    public String showEditEmployeeForm(@PathVariable("id") Long id, Model model) {
        Optional<Employees> employees = employeesService.findById(id);
        if (employees.isPresent()) {
            Employees employee = employees.get();
            List<EmployeesSkill> skills = employee.getSkills();
            EmployeesRequest employeesRequest = employee.toDto();
            List<EmployeesSkillRequest> skillRequests = skills.stream()
                    .map(EmployeesSkill::toDto)
                    .collect(Collectors.toList());
            employeesRequest.setEmployeesSkillRequests(skillRequests);
            model.addAttribute("employeesRequest", employeesRequest);
            model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
            model.addAttribute("skills", skillsService.getAllSkills());
            return "employees/editemployee";
        } else {
            return "redirect:/employees";
        }
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 BindingResult result) throws IOException {
        if (!imageFile.isEmpty()) {
            String imagePath = employeesService.saveImage(imageFile);

            employeesRequest.setImage(imagePath);
        } else {
            employeesRequest.setImage(employeesRequest.getExistingImage());
        }
        Employees employee = employeesRequest.toEntity();
        employeesService.save(employee);
        employeesSkillService.deleteByEmployeeId(employee.getEmployeeId());


        if (employeesRequest.getEmployeesSkillRequests() != null) {
            for (EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesSkillRequests()) {
                EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee);
                employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill);
            }
        }

        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        if (employeesService.isallocation1(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        employeesService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}