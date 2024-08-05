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
    private Employees employee;
    private Skills skill;
    //8월 3일 21:23 소수점 값을 더해주기 위해서 Integer -> Double 변경하였습니다.
    private Double skillCareer;

    public EmployeesSkill toEntity(Employees employee) {
        EmployeesSkill employeesSkill = new EmployeesSkill();
        employeesSkill.setEmployeesSkillId(this.employeesSkillId);
        employeesSkill.setEmployee(employee);
        employeesSkill.setSkill(this.skill);
        employeesSkill.setSkillCareer(this.skillCareer);
        return employeesSkill;

    }
}
