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


    //7월 30일 14:49 사진 추가되는 사원 등록 페이지로 시도중
    @GetMapping("/newemployee")
    public String showCreateEmployeeFormWithPhoto(Model model) {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        model.addAttribute("employeesRequest", employeesRequest);
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "employees/createemployee";
    }

    //7월 30일 14:49 사진 추가되는 사원 등록 페이지로 시도중
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



    //7월 30일 15:43 수정 코드
    @GetMapping("/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        Optional<Employees> employees = employeesService.findById(id);

        if (employees.isPresent()) {
            Employees employee = employees.get();
            // 사원의 ID 출력
            System.out.println("디버깅: 엔티티의 ID등의 정보들이 설정되었는지 확인");
            System.out.println("Employee ID: " + employee.getEmployeeId());
            System.out.println("Employee latitude: " + employee.getLatitude());
            System.out.println("Employee longitude: " + employee.getLongitude());
            System.out.println("Employee image: " + employee.getImage());


            // 사원의 프로그래밍 경력 출력
            List<EmployeesSkill> skills = employee.getSkills();
            for (EmployeesSkill skill : skills) {
                System.out.println("SkillID: " + skill.getSkill().getSkillId() + ", Skill: " + skill.getSkill().getSkillName() + ", Career: " + skill.getSkillCareer());
            }

            model.addAttribute("employeesRequest", employee.toDto());
            //System.out.println("Employee image: " + employees );
            model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
            model.addAttribute("skills", skillsService.getAllSkills());
            return "employees/editEmployee";
        } else {
            return "redirect:/employees";
        }
    }


    //  7월 30일 16:17 기존 코드
    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 BindingResult result) throws IOException {
        if (!imageFile.isEmpty()) {
            String imagePath = employeesService.saveImage(imageFile);
            System.out.println("update요청시 imagePath나오나? " + imagePath);
            employeesRequest.setImage(imagePath);
            //Employees employee = employeesService.save(employeesRequest.toEntity(imagePath)); <-create에서는 이렇게
        } else {
            // 새로운 이미지가 업로드되지 않은 경우
            employeesRequest.setImage(employeesRequest.getExistingImage());
        }

        Employees employee = employeesRequest.toEntity();

        // 디버깅: 엔티티의 ID가 설정되었는지 확인
        System.out.println("디버깅: 엔티티의 ID등의 정보들이 설정되었는지 확인");
        System.out.println("Employee ID: " + employee.getEmployeeId());
        System.out.println("Employee latitude: " + employee.getLatitude());
        System.out.println("Employee longitude: " + employee.getLongitude());
        System.out.println("Employee image: " + employee.getImage());

        employeesService.save(employee);  // ID가 있는 경우 업데이트, 없는 경우 새로 추가

        if (employeesRequest.getEmployeesSkillRequests() != null) {
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