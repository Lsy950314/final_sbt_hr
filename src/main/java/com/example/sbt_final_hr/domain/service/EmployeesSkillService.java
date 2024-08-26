package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
//import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeesSkillService {

    private final EmployeesSkillRepository employeesSkillRepository;
    private final EmployeesRepository employeeRepository;
    private final SkillsRepository skillRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public EmployeesSkillService(EmployeesSkillRepository employeesSkillRepository,
                                 EmployeesRepository employeeRepository,
                                 SkillsRepository skillRepository,
                                 PlatformTransactionManager transactionManager
                                 ) {
        this.employeesSkillRepository = employeesSkillRepository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.transactionManager = transactionManager;
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

    @Transactional
    public void deleteByEmployeeId(Long employeeId) {
        employeesSkillRepository.deleteByEmployeeEmployeeId(employeeId);
    }

    public void updateSkillCareerOfProjectParticipants(List<Map<String, Object>> projectParticipantsInfos) {
        for (Map<String, Object> participantInfo : projectParticipantsInfos) {
            Long employeeId = ((Number) participantInfo.get("employeeId")).longValue();
            Long skillId = ((Number) participantInfo.get("skillId")).longValue();
            Double projectDuration = ((Number) participantInfo.get("projectDuration")).doubleValue();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                employeesSkillRepository.updateSkillCareerOfProjectParticipants(employeeId, skillId, projectDuration);
                transactionManager.commit(status);
            } catch (Exception e) {
                transactionManager.rollback(status);
                throw e;
            }
        }
    }


}
