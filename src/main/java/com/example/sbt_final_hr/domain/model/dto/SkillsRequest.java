package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillsRequest {
    private Long skillId;
    private String skillName; // 변경된 필드명

    public Skills toEntity() {
        Skills skills = new Skills();
        skills.setSkillName(this.skillName);
        skills.setSkillId(this.skillId);
        return skills;
    }

}
