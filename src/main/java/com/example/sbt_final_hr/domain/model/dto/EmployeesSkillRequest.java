package com.example.sbt_final_hr.domain.model.dto;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeesSkillRequest {
    private Long employeesSkillId;
    private Long employeeId;
    private Long skillLanguage;
    private Integer skillCareer;

    private String skillName;  // 프로그래밍 언어명 때문에 추가된 부분
    private String Name;// 직원이름 때문에 추가된 부분

    public EmployeesSkill toEntity(Employees employee, Skills skill) {
        EmployeesSkill employeesSkill = new EmployeesSkill();
        employeesSkill.setEmployeesSkillId(this.employeesSkillId);
        employeesSkill.setEmployee(employee); // 외래 키 대신 엔티티 참조 설정
        employeesSkill.setSkill(skill); // 외래 키 대신 엔티티 참조 설정
        employeesSkill.setSkillCareer(this.skillCareer);
        return employeesSkill;
    }
    //맞는지 모르겠다.

    // 새롭게 추가된 toDto 메서드

}
