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
//    아래처럼 toEntity()에서 변환만 잘 이루어진다면 반드시 entity의 속성명과 같을 필요는 없음
//    private Long id; // 
//    private String name; //
//
//    public Skills toEntity() {
//        Skills skills = new Skills();
//        skills.setSkillId(this.id);
//        skills.setSkillName(this.name);
//        return skills;
//    }
}
