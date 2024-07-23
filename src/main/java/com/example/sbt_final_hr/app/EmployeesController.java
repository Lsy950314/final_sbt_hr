package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.ProjectTypesService;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeesController {
    private EmployeesService employeesService;
    private ProjectTypesService projectTypesService;
    private SkillsService skillsService;

    @Autowired
    public EmployeesController(EmployeesService employeesService, ProjectTypesService projectTypesService, SkillsService skillsService) {
        this.employeesService = employeesService;
        this.projectTypesService = projectTypesService;
        this.skillsService = skillsService;
    }

    @GetMapping("/newemployee")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employeesRequest", new EmployeesRequest());
        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());
        return "employees/createemployee";
    }

    @PostMapping("/createemployee")
    public String createEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest, BindingResult result) {
        try {
            employeesService.save(employeesRequest.toEntity());
        } catch (Exception e) {
            result.rejectValue("photo", "error.employeesRequest", "Failed to process the photo.");
            return "employees/createemployee";
        }
        return "redirect:/employees";
    }



    // READ: 직원 리스트
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

    //UPDATE:
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
    public String updateEmployee(@ModelAttribute("employeesRequest") EmployeesRequest employeesRequest,BindingResult result) {
        employeesService.save(employeesRequest.toEntity());
        return "redirect:/employees";
    }

    // DELETE:
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesService.deleteById(id);
        return "redirect:/employees";
    }
}
