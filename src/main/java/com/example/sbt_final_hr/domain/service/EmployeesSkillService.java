package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeesSkillService {

    private final EmployeesSkillRepository employeesSkillRepository;
    private final EmployeesRepository employeeRepository;
    private final SkillsRepository skillRepository;

    @Autowired
    public EmployeesSkillService(EmployeesSkillRepository employeesSkillRepository,
                                 EmployeesRepository employeeRepository,
                                 SkillsRepository skillRepository) {
        this.employeesSkillRepository = employeesSkillRepository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    public List<EmployeesSkillRequest> getAllEmployeesSkills() {
        return employeesSkillRepository.findAll().stream()
                .map(EmployeesSkill::toDto)
                .collect(Collectors.toList());
    }

    public EmployeesSkillRequest getEmployeesSkillById(Long id) {
        EmployeesSkill employeesSkill = employeesSkillRepository.findById(id).orElse(null);
        return employeesSkill != null ? employeesSkill.toDto() : null;
    }

    public void createOrUpdateEmployeesSkill(EmployeesSkillRequest dto) {
        Employees employee = employeeRepository.findById(dto.getEmployeesSkillId()).orElseThrow(() -> new RuntimeException("Employee not found"));
        Skills skill = skillRepository.findById(dto.getEmployeesSkillId()).orElseThrow(() -> new RuntimeException("Skill not found"));

        EmployeesSkill employeesSkill = dto.toEntity(employee);
        employeesSkillRepository.save(employeesSkill);
    }

    public void deleteEmployeesSkill(Long id) {
        employeesSkillRepository.deleteById(id);
    }

    public void createOrUpdateEmployeesSkill(EmployeesSkill employeesSkill) {
        employeesSkillRepository.save(employeesSkill);
    }

    //7월31일 12:50 추가된 메서드
//    public List<EmployeesSkillRequest> getSkillsByEmployeeId(Long employeeId) {
//        return employeesSkillRepository.findByEmployeeEmployeeId(employeeId).stream()
//                .map(EmployeesSkill::toDto)
//                .collect(Collectors.toList());
//    }
    //7월31일 13:39 추가
    @Transactional
    public void deleteByEmployeeId(Long employeeId) {
        employeesSkillRepository.deleteByEmployeeEmployeeId(employeeId);
    }










}
