package com.example.sbt_final_hr.app;


import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeesController {
    @Autowired
    private EmployeesService employeesService;

    @GetMapping("/geocoding")
    public String geoCodingAPIPractice() {
        return "GeocodingAPIPractice";
    }

    @GetMapping("/places")
    public String placeAPIPractice() {
        return "placesAPIPractice";
    }

    //Create
    @GetMapping("/newemployee")
    public String newEmployee(Model model) {
        model.addAttribute("employeesRequest", new EmployeesRequest());
        return "createemployee";
    }



    @PostMapping("/createemployee")
    public String createEmployee(@ModelAttribute("employeesRequest")EmployeesRequest employeesRequest, BindingResult result) {
        if (result.hasErrors()) {
            return "createEmployee";
        }
        employeesService.save(employeesRequest.toEntity());
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
        return "employees/employeesList";
    }




}
