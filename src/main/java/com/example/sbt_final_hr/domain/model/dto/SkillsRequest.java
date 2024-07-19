package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillsRequest {
    private String skill_name; // 변경된 필드명

    public Skills toEntity() {
        Skills skills = new Skills();
        skills.setSkill_name(skill_name);
        return skills;
    }
}
