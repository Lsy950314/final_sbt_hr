package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.EmployeesSkillService;
import com.example.sbt_final_hr.domain.service.ProjectTypesService;
import com.example.sbt_final_hr.domain.service.SkillsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    //7월 26일 15시 새로운 시도중
    //팀장 코드 참고해서 수정중-get
    @GetMapping("/newemployee")
    public String showCreateEmployeeForm(Model model) {
        EmployeesRequest employeesRequest = new EmployeesRequest();
        model.addAttribute("employeesRequest", employeesRequest);
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "employees/createemployee";
    }

    //7월 26일 15시 새로운 시도중
    //내가만든 개쓰레기 코드-post
//    @PostMapping("/createemployee")
//    public String createEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) {
//        try {
//            Employees employee = employeesService.save(employeesRequest.toEntity());
//            for (EmployeesRequest.ProgrammingExperience skillDTO : employeesRequest.getSkills()) {
//                EmployeesSkillRequest skillRequest = new EmployeesSkillRequest();
//                skillRequest.setEmployeeId(employee.getEmployeeId());
//                skillRequest.setSkillLanguage(skillDTO.getSkillLanguage());
//                skillRequest.setSkillCareer(skillDTO.getSkillCareer());
//                employeesSkillService.createOrUpdateEmployeesSkill(skillRequest);
//            }
//        } catch (Exception e) {
//            result.rejectValue("photo", "error.employeesRequest", "Failed to process the photo.");
//            return "employees/createemployee";
//        }
//        return "redirect:/employees";
//    }
    //7월 26일 15시 새로운 시도중
    //팀장 코드 참고해서 수정중-post
    @PostMapping("/createemployee")
    public String createEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) {
        Employees employee = employeesService.save(employeesRequest.toEntity());
        if(employeesRequest.getEmployeesskill() != null) {
            for(EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesskill() ) {



            }


        }
        return "redirect:/employees";
    }
    //7월 26일 16시 50분 새로운 시도중
//    @PostMapping("/createemployee")
//    public String createEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) {
//        Employees employee = employeesService.save(employeesRequest.toEntity());
//        if (employeesRequest.getEmployeesskill() != null) {
//            for (EmployeesSkillRequest employeesSkillRequest : employeesRequest.getEmployeesskill()) {
//                // skillId를 통해서 skill 객체를 불러옴
//                Optional<Skills> optionalSkill = skillsService.findById(employeesSkillRequest.getSkillLanguage());
//                if (optionalSkill.isPresent()) {
//                    EmployeesSkill employeesSkill = employeesSkillRequest.toEntity(employee, optionalSkill.get());
//                    employeesSkillService.createOrUpdateEmployeesSkill(employeesSkill.toDto());
//                }
//            }
//        }
//        return "redirect:/employees";
//    }





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

    @GetMapping("/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        Optional<Employees> employees = employeesService.findById(id);
        if (employees.isPresent()) {
            model.addAttribute("employeesRequest", employees.get().toDto());
            model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
            model.addAttribute("skills", skillsService.getAllSkills());
            return "employees/editEmployee";
        } else {
            return "redirect:/employees";
        }
    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) {
        employeesService.save(employeesRequest.toEntity());
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesService.deleteById(id);
        return "redirect:/employees";
    }

    //Employee테이블, Employee_Skill테이블 모두에 튜플 삽입 가능한 create 메서드 추가 시도중.













}