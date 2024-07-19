package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.SkillsRequest;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SkillsController {

    private final SkillsService skillsService;

    public SkillsController(SkillsService skillsService) {
        this.skillsService = skillsService;
    }

    @GetMapping("/skillUpdate")
    public String skillUpdateController(Model model, @RequestParam int id) {
        model.addAttribute("skills", skillsService.getSkillsById(id));
        SkillsRequest skillsRequest = new SkillsRequest();
        model.addAttribute("skillsRequest", skillsRequest);
        return "skill/skillUpdate";
    }

    @PostMapping
    public String skillUpdateController(@ModelAttribute("skillsRequest") SkillsRequest skillsRequest) {
        skillsService.saveSkills(skillsRequest);
        return "redirect:/skillList";
    }

    @GetMapping("/skillList")
    public String getSelectAllSkills(Model model) {
        model.addAttribute("skills", skillsService.getAllSkills());
        return "skill/skillList";
    }

    @GetMapping("/insertSkill")
    public String getInsertSkill(Model model) {
        SkillsRequest skillsRequest = new SkillsRequest();
        model.addAttribute("skillRequest", skillsRequest);
        return "skill/insertSkill";
    }

    @PostMapping("/insertSkill")
    public String postInsertSkill(@ModelAttribute("skillRequest") SkillsRequest skillsRequest) {
        skillsService.saveSkills(skillsRequest);
        return "skill/insertSkill";
    }

}
