package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.EmployeesSkillService;
import com.example.sbt_final_hr.domain.service.ProjectTypesService;
import com.example.sbt_final_hr.domain.service.SkillsService;
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
    private EmployeesService employeesService;
    private ProjectTypesService projectTypesService;
    private SkillsService skillsService;
    private EmployeesSkillService employeesSkillService;

    @Autowired
    public EmployeesController(EmployeesService employeesService, ProjectTypesService projectTypesService, SkillsService skillsService, EmployeesSkillService employeesSkillService) {
        this.employeesService = employeesService;
        this.projectTypesService = projectTypesService;
        this.skillsService = skillsService;
        this.employeesSkillService = employeesSkillService;
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

    @PostMapping("/getModalData")
    public ResponseEntity<Map<String, Object>> getEmployeeModalData(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Optional<Employees> employees = employeesService.findById(id);
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