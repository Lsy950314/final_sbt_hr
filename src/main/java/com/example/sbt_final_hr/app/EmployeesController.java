package com.example.sbt_final_hr.app;


import com.example.sbt_final_hr.domain.model.dto.EmployeesPracticeRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.dto.SkillsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.sbt_final_hr.domain.service.ProjectTypesService;
import java.util.List;

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


    @GetMapping("/geocoding")
    public String geoCodingAPIPractice() {
        return "practice/GeocodingAPIPractice";
    }

    @GetMapping("/places")
    public String placeAPIPractice() {
        return "practice/placesAPIPractice";
    }


    @GetMapping("/newemployee")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employeesRequest", new EmployeesRequest());

        model.addAttribute("projectTypes", projectTypesService.getAllProjectTypes());
        model.addAttribute("skills", skillsService.getAllSkills());

        return "employees/createemployee";
    }


//    @PostMapping("/createemployee")
//    public String createEmployee(@ModelAttribute("employeesRequest")EmployeesRequest employeesRequest,Model model) {
//        employeesService.save(employeesRequest.toEntity());
//
//        return "redirect:/employees";
//    }

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


    //READ: 직원 리스트
    @GetMapping
    public String listEmployees(@RequestParam(name = "name", required = false) String name, Model model) {
        if (name != null && !name.isEmpty()) {
            model.addAttribute("employees", employeesService.findByName(name));
        } else {
            model.addAttribute("employees", employeesService.findAll());
        }
        return "employees/employeeslist";
    }

    //DELETE:
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesService.deleteById(id);
        return "redirect:/employees";
    }




}
