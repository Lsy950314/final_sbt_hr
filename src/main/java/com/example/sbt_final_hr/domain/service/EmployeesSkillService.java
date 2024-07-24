package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeesSkillService {

    @Autowired
    private EmployeesSkillRepository employeesSkillRepository;

    @Autowired
    private EmployeesRepository employeeRepository;

    @Autowired
    private SkillsRepository skillRepository;

    public List<EmployeesSkillRequest> getAllEmployeesSkills() {
        return employeesSkillRepository.findAll().stream()
                .map(EmployeesSkill::toDto)
                .collect(Collectors.toList());
    }

    public EmployeesSkillRequest getEmployeesSkillById(Long id) {
        EmployeesSkill employeesSkill = employeesSkillRepository.findById(id).orElse(null);
        return employeesSkill != null ? employeesSkill.toDto() : null;
    }

    public EmployeesSkillRequest createOrUpdateEmployeesSkill(EmployeesSkillRequest dto) {
        Employees employee = employeeRepository.findById(dto.getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found"));
        Skills skill = skillRepository.findById(dto.getSkillLanguage()).orElseThrow(() -> new RuntimeException("Skill not found"));

        EmployeesSkill employeesSkill = dto.toEntity(employee, skill);
        EmployeesSkill savedEntity = employeesSkillRepository.save(employeesSkill);
        return savedEntity.toDto();
    }

    public void deleteEmployeesSkill(Long id) {
        employeesSkillRepository.deleteById(id);
    }
}
