package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import com.example.sbt_final_hr.domain.service.EmployeesService;
import com.example.sbt_final_hr.domain.service.EmployeesSkillService;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/employees-skills")
public class EmployeesSkillController {
    private EmployeesSkillService employeesSkillService;
    private SkillsService skillsService;
    private EmployeesService employeesService;

    @Autowired
    private EmployeesSkillController(EmployeesService employeesService, SkillsService skillsService, EmployeesSkillService employeesSkillService){
        this.employeesService = employeesService;
        this.skillsService = skillsService;
        this.employeesSkillService = employeesSkillService;

    }



    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private SkillsRepository skillsRepository;


    @GetMapping("/skills2")
    public String getAllEmployeesSkills(Model model) {
        List<EmployeesSkillRequest> employeesskills = employeesSkillService.getAllEmployeesSkills();
        model.addAttribute("employeesskills", employeesskills);

        return "employees/employees-skills2";
    }

    @GetMapping
    public String getAllEmployeesSkills2(Model model) {
        List<EmployeesSkillRequest> employeesskills = employeesSkillService.getAllEmployeesSkills();
        model.addAttribute("employeesskills", employeesskills);

        return "employees/employees-skills";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("skill", new EmployeesSkillRequest());
        return "employees/create-employees-skill";
    }

    @PostMapping
    public String createEmployeesSkill(@ModelAttribute EmployeesSkillRequest employeesSkillRequest) {
        employeesSkillService.createOrUpdateEmployeesSkill(employeesSkillRequest);
        return "redirect:/employees-skills";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        EmployeesSkillRequest skill = employeesSkillService.getEmployeesSkillById(id);
        model.addAttribute("skill", skill);
        return "employees/edit-employees-skill";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployeesSkill(@PathVariable Long id, @ModelAttribute EmployeesSkillRequest employeesSkillRequest) {
        employeesSkillRequest.setEmployeesSkillId(id);
        employeesSkillService.createOrUpdateEmployeesSkill(employeesSkillRequest);
        return "redirect:/employees-skills";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployeesSkill(@PathVariable Long id) {
        employeesSkillService.deleteEmployeesSkill(id);
        return "redirect:/employees-skills";
    }
}