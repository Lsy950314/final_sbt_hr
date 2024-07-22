package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.entity.EmployeesPractice;
import com.example.sbt_final_hr.domain.service.EmployeesPracticeService;
import com.example.sbt_final_hr.domain.model.dto.EmployeesPracticeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/employeespractice")
public class EmployeesPracticeController {

    @Autowired
    private EmployeesPracticeService employeesPracticeService;

    @GetMapping("/new")
    public String showCreateEmployeeForm(Model model) {
        model.addAttribute("employeeRequest", new EmployeesPracticeRequest());
        return "createEmployee";
    }

    @PostMapping("/create")
    public String createEmployee(@ModelAttribute("employeeRequest") EmployeesPracticeRequest employeesPracticeRequest, BindingResult result) {
        if (result.hasErrors()) {
            return "createEmployee";
        }
        employeesPracticeService.save(employeesPracticeRequest.toEntity());
        return "redirect:/employees";
    }

//    @GetMapping
//    public String listEmployees(Model model) {
//        model.addAttribute("employees", employeesPracticeService.findAll());
//        return "employeesList";
//    }

    @GetMapping
    public String listEmployees(@RequestParam(name="name", required = false)String name, Model model) {
        if(name != null && !name.isEmpty()) {
            model.addAttribute("employees", employeesPracticeService.findByName(name));
        } else {
            model.addAttribute("employees", employeesPracticeService.findAll());
        }
        return "employeesList";
    }







//    @GetMapping("/edit/{id}")
//    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
//        Optional<EmployeesPractice> employeesPractice = employeesPracticeService.findById(id);
//        if (employeesPractice.isPresent()) {
//            model.addAttribute("employeeRequest", employeesPractice.get().toDto());
//            return "editEmployee";
//        } else {
//            return "redirect:/employees";
//        }
//    }

    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute("employeeRequest") EmployeesPracticeRequest employeesPracticeRequest, BindingResult result) {
        if (result.hasErrors()) {
            return "editEmployee";
        }
        employeesPracticeService.save(employeesPracticeRequest.toEntity());
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeesPracticeService.deleteById(id);
        return "redirect:/employees";
    }
}
