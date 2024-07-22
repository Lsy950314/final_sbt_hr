package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.SkillsRequest;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.service.SkillsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class SkillsController {
    

    private final SkillsService skillsService;

    public SkillsController(SkillsService skillsService) {
        this.skillsService = skillsService;
    }

    @PostMapping("/skillDelete")
    public ResponseEntity<Void> skillDelete(@RequestBody Map<String, String> payload) {
        Long id = Long.parseLong(payload.get("id"));
        skillsService.deleteSkills(id);
        return ResponseEntity.ok().build(); // 상태 코드 200 OK 반환
    }


    @GetMapping("/skillUpdate")
    public String skillUpdateController(Model model, @RequestParam Map<String, String> payload) {
        // `payload`: `{"id":"pk"}` 이 경우에는 키-밸류 쌍이 하나 뿐일 것
        long id = Long.parseLong(payload.get("id"));
        Skills skills = skillsService.getSkillsById((long) id);
        SkillsRequest skillsRequest = new SkillsRequest();
        skillsRequest.setSkillId(skills.getSkillId());
        skillsRequest.setSkillName(skills.getSkillName());
        model.addAttribute("skillsRequest", skillsRequest);
        return "skill/skillUpdate";
    }

    @PostMapping("/skillUpdate")
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
        return "redirect:/skillList";
    }

}
