//package com.example.sbt_final_hr.app;
//
//import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
//import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
//import com.example.sbt_final_hr.domain.model.entity.Employees;
//import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
//import com.example.sbt_final_hr.domain.service.EmployeesService;
//import com.example.sbt_final_hr.domain.service.EmployeesSkillService;
//import com.example.sbt_final_hr.domain.service.ProjectTypesService;
//import com.example.sbt_final_hr.domain.service.SkillsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/employees")
//public class EmployeesController복구용 {
//    private EmployeesService employeesService;
//    private ProjectTypesService projectTypesService;
//    private SkillsService skillsService;
//    private EmployeesSkillService employeesSkillService;
//
//    @Autowired
//    public EmployeesController복구용(EmployeesService employeesService, ProjectTypesService projectTypesService, SkillsService skillsService, EmployeesSkillService employeesSkillService) {
//        this.employeesService = employeesService;
//        this.projectTypesService = projectTypesService;
//        this.skillsService = skillsService;
//        this.employeesSkillService = employeesSkillService;
//    }
//
//    @GetMapping("/newemployee")
//    public String showCreateEmployeeForm(Model model) {
//        EmployeesRequest employeesRequest = new EmployeesRequest();
//        model.addAttribute("employeesRequest", employeesRequest);
//        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
//        model.addAttribute("skills", skillsService.getAllSkills());
//        return "employees/createemployee";
//    }
//    //7월 30일 12:43 사진 추가되는 사원 등록 페이지로 시도중
//    @GetMapping("/newemployeewithphoto")
//    public String showCreateEmployeeFormWithPhoto(Model model) {
//        EmployeesRequest employeesRequest = new EmployeesRequest();
//        model.addAttribute("employeesRequest", employeesRequest);
//        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
//        model.addAttribute("skills", skillsService.getAllSkills());
//        return "employees/createemployee(photo)";
//    }
//
//
//
//
//    @PostMapping("/createemployee")
//    public String createEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest) throws IOException {
//        Employees employee = employeesService.save(employeesRequest.toEntity());
//        if(employeesRequest.getEmployeesSkillRequests() != null) {
//            for(EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesSkillRequests()) {
//                EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee);
//                employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill);
//            }
//        }
//        return "redirect:/employees";
//    }
//
//    //7월 30일 12:43 사진 추가되는 사원 등록 페이지로 시도중
//    @PostMapping("/createemployee2")
//    public String createEmployeeWithPhoto(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest) throws IOException {
//        Employees employee = employeesService.save(employeesRequest.toEntity());
//        if(employeesRequest.getEmployeesSkillRequests() != null) {
//            for(EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesSkillRequests()) {
//                EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee);
//                employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill);
//            }
//        }
//        return "redirect:/employees";
//    }
//
//
//    @GetMapping
//    public String listEmployees(@RequestParam(name = "name", required = false) String name, Model model) {
//        if (name != null && !name.isEmpty()) {
//            model.addAttribute("employees", employeesService.findByName(name));
//        } else {
//            model.addAttribute("employees", employeesService.findAll());
//        }
//        return "employees/employeeslist";
//    }
//
//    @GetMapping("/list2")
//    public String listEmployees2(@RequestParam(name = "name", required = false) String name, Model model) {
//        if (name != null && !name.isEmpty()) {
//            model.addAttribute("employees", employeesService.findByName(name));
//        } else {
//            model.addAttribute("employees", employeesService.findAll());
//        }
//        return "Employees_practice/employees";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
//        Optional<Employees> employees = employeesService.findById(id);
//        if (employees.isPresent()) {
//            model.addAttribute("employeesRequest", employees.get().toDto());
//            model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
//            model.addAttribute("skills", skillsService.getAllSkills());
//            return "employees/editEmployee";
//        } else {
//            return "redirect:/employees";
//        }
//    }
//
//    @PostMapping("/update")
//    public String updateEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) throws IOException {
//        employeesService.save(employeesRequest.toEntity());
//        return "redirect:/employees";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteEmployee(@PathVariable Long id) {
//        employeesService.deleteById(id);
//        return "redirect:/employees";
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//}