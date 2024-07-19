package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.Employees_practice;
import com.example.sbt_final_hr.domain.service.Employees_practiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class Employees_practiceController {

    private final Employees_practiceService employees_practiceService;
    public Employees_practiceController(Employees_practiceService employees_practiceService) {
        this.employees_practiceService = employees_practiceService;
    }

    //read화면
    @GetMapping("/employeesList")
    public String EmployeesList(Model model) {
        List<Employees_practice> Employees = employees_practiceService.findAll();
        model.addAttribute("Employees", Employees);
        for (Employees_practice employee : Employees) {
            System.out.println(employee.getCurrentProjectEndDate());
            System.out.println(employee.getLastProjectEndDate());

        }

        return "Employees_practice/employeesList";
    }




}
