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
    private String skillName;
    private String name;

    @Getter
    @Setter
    public static class SkillDTO {
        private Long skillLanguage;
        private Integer skillCareer;
    }

    public EmployeesSkill toEntity(Employees employee, Skills skill) {
        EmployeesSkill employeesSkill = new EmployeesSkill();
        employeesSkill.setEmployeesSkillId(this.employeesSkillId);
        employeesSkill.setEmployee(employee);
        employeesSkill.setSkill(skill);
        employeesSkill.setSkillCareer(this.skillCareer);
        return employeesSkill;
    }
}
