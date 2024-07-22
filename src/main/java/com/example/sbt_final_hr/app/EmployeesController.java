package com.example.sbt_final_hr.app;


import com.example.sbt_final_hr.domain.model.dto.EmployeesPracticeRequest;
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
        return "practice/GeocodingAPIPractice";
    }

    @GetMapping("/places")
    public String placeAPIPractice() {
        return "practice/placesAPIPractice";
    }

    //Create
//    @GetMapping("/test")
//    public String test() {
//        return "employees/test";
//    }

    @GetMapping("/newemployee")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employeesRequest", new EmployeesRequest());
        return "employees/createemployee";
    }


    @PostMapping("/createemployee")
    public String createEmployee(@ModelAttribute("employeesRequest")EmployeesRequest employeesRequest, BindingResult result) {
//        if (result.hasErrors()) {
//            System.out.println("직원 등록 실패 ");
//            return "employees/createemployee";
//        }
        employeesService.save(employeesRequest.toEntity());
//        System.out.println("직원 등록 성공 ");
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

    //DELETE:
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesService.deleteById(id);
        return "redirect:/employees";
    }




}
