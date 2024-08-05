package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.*;
import com.example.sbt_final_hr.domain.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String listEmployees(@RequestParam(name = "name", required = false) String name, Model model) {
        if (name != null && !name.isEmpty()) {
            model.addAttribute("employees", employeesService.findByName(name));
        } else {
            model.addAttribute("employees", employeesService.findAll());
        }
        return "employees/employeeslist";
    }

    @GetMapping("/list2")
    public String listEmployees2(@RequestParam(name = "name", required = false) String name, Model model) {
        if (name != null && !name.isEmpty()) {
            model.addAttribute("employees", employeesService.findByName(name));
        } else {
            model.addAttribute("employees", employeesService.findAll());
        }
        return "Employees_practice/employees";
    }
    //8월 5일 13:00 부터 getEmployeeModalData 메서드 수정 시작
    @PostMapping("/getModalData")
    public ResponseEntity<Map<String, Object>> getEmployeeModalData(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Optional<Employees> employees = employeesService.findById(id);
        List<EmployeesProjects> employeesProjects =  employeesProjectsService.findByEmployeeId(id);
        List<Long> projectIds = new ArrayList<>();

        // Printing the retrieved projects for debugging
        System.out.println("Employee Projects:");
        for (EmployeesProjects project : employeesProjects) {
            Long projectId = project.getProject().getProjectId();
            System.out.println("Project ID: " + project.getProject().getProjectId());
            projectIds.add(projectId);
        }
        //13:18 콘솔에 찍히니까 모달에 적절하게 보이게 할 것.
//        List<Map<String, Object>> skills = new ArrayList<>();
//        for (EmployeesSkill skill : employeeSkills) {
//            Map<String, Object> skillInfo = new HashMap<>();
//            skillInfo.put("skillName", skill.getSkill().getSkillName());
//            skillInfo.put("skillCareer", skill.getSkillCareer());
//            skills.add(skillInfo);
//        }
//        response.put("skills", skills);

        // 프로젝트 정보를 가져옴
        List<Projects> projects = projectsService.findByProjectIds(projectIds);
        System.out.println("Projects Details:");
        for (Projects project : projects) {
            System.out.println("Project ID: " + project.getProjectId());
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Work Location: " + project.getWorkLocation());
            System.out.println("Client Company: " + project.getClientCompany());
            System.out.println("Start Date: " + project.getStartDate());
            System.out.println("End Date: " + project.getEndDate());
            System.out.println("Status: " + project.getStatus());
            System.out.println("Latitude: " + project.getLatitude());
            System.out.println("Longitude: " + project.getLongitude());
            System.out.println("Contact Phone: " + project.getContactPhone());
            System.out.println("Contact Name: " + project.getContactName());
            System.out.println("Registration Date: " + project.getRegistrationDate());
            System.out.println("Project Type: " + project.getProjectType().getProjectTypeName());
            System.out.println("Total Project Duration in Months: " + project.getTotalProjectDurationInMonths());
        }

        // 프로젝트 정보를 응답에 추가






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

        List<Map<String, Object>> skills = new ArrayList<>();
        for (EmployeesSkill skill : employeeSkills) {
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("skillName", skill.getSkill().getSkillName());
            skillInfo.put("skillCareer", skill.getSkillCareer());
            skills.add(skillInfo);
        }
        response.put("skills", skills);

        //8월 5일 15:31 추가는 했는데 테스트는 안해봄.
        List<Map<String, Object>> projectInfos = new ArrayList<>();
        for (Projects project : projects) {
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
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        Optional<Employees> employees = employeesService.findById(id);
        if (employees.isPresent()) {
            Employees employee = employees.get();
            // 사원의 프로그래밍 경력 출력
            List<EmployeesSkill> skills = employee.getSkills();
            EmployeesRequest employeesRequest = employee.toDto();
            List<EmployeesSkillRequest> skillRequests = skills.stream()
                    .map(EmployeesSkill::toDto)
                    .collect(Collectors.toList());
            employeesRequest.setEmployeesSkillRequests(skillRequests);
            model.addAttribute("employeesRequest", employeesRequest);
            model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
            model.addAttribute("skills", skillsService.getAllSkills());
            return "employees/editEmployee";
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
//            System.out.println("update요청시 imagePath나오나? " + imagePath);
            employeesRequest.setImage(imagePath);
        } else {// 새로운 이미지가 업로드되지 않은 경우
            employeesRequest.setImage(employeesRequest.getExistingImage());
        }
        Employees employee = employeesRequest.toEntity();
        employeesService.save(employee);  // ID가 있는 경우 업데이트, 없는 경우 새로 추가
        employeesSkillService.deleteByEmployeeId(employee.getEmployeeId());// 기존 스킬 삭제
        if (employeesRequest.getEmployeesSkillRequests() != null) {// 새로운 스킬 저장
            for (EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesSkillRequests()) {
                EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee);
                employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill);
            }
        }

        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesService.deleteById(id);
        return "redirect:/employees";
    }



}