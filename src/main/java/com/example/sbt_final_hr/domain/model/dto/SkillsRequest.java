package com.example.sbt_final_hr.domain.model.dto;


import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillsRequest {
    private long skillId;
    private String skillName;

    public Skills toEntity(){
        Skills skills = new Skills();
        skills.setSkillName(skillName);
        skills.setSkillId(skillId);
        return skills;
    }
}
