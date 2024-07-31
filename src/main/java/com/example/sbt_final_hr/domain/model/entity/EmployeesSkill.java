package com.example.sbt_final_hr.domain.model.entity;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "employees_skill", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"Employee_ID", "Skill_Language"})
})
public class EmployeesSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="employees_skill_seq")
    @SequenceGenerator(name = "employees_skill_seq", sequenceName = "Employees_Skill_SEQ", allocationSize = 1)
    @Column(name = "Employees_Skill_ID")
    private Long employeesSkillId;

    @ManyToOne
    @JoinColumn(name = "Employee_ID", nullable = false)
    private Employees employee;
    //chatgpt는 employeeId 말고 employee 이렇게 쓰라는데 모르겠네. DB컬럼명과 혼동된다고해서.
    //employeeId->employee

    @ManyToOne
    @JoinColumn(name = "Skill_Language", nullable = false)
    private Skills skill;
    //chatgpt는 skillLanguage 말고 skill 이렇게 쓰라는데 모르겠네. DB컬럼명과 혼동된다고해서.
    //skillLanguage->skill
    @Column(name = "Skill_Career", nullable = false)
    private int skillCareer;
//
//    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
//    private List<EmployeesSkill> skills;

    public EmployeesSkillRequest toDto() {
        EmployeesSkillRequest employeesSkillRequest = new EmployeesSkillRequest();
        employeesSkillRequest.setEmployeesSkillId(this.employeesSkillId);
        employeesSkillRequest.setEmployee(this.employee);
        employeesSkillRequest.setSkill(this.skill);
        employeesSkillRequest.setSkillCareer(this.skillCareer);
        return employeesSkillRequest;
    }





}



