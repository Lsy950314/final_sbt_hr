package com.example.sbt_final_hr.app;


import com.example.sbt_final_hr.domain.model.dto.EmployeesPracticeRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String listEmployees(Model model) {
        List<Employees> employeesList = employeesService.findAll();
        model.addAttribute("employees", employeesList);
        return "employees/employeesList";
    }




}
