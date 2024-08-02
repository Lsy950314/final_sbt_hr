package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    private final EmployeesRepository employeesRepository;
    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository, EmployeesRepository employeesRepository) {
        this.employeesProjectsRepository = employeesProjectsRepository;
        this.employeesRepository = employeesRepository;
    }

    public List<EmployeesProjects> getAllEmployeesProjects() {
        return employeesProjectsRepository.findAll();
    }

    public List<EmployeesProjects> getEmployeesProjectByProjectId(Long id) {
        return employeesProjectsRepository.findByProject_ProjectId(id);
    }

    public List<Employees> getEmployeesByProjectId(Long projectId) {
        List<EmployeesProjects> employeesProjects = employeesProjectsRepository.findByProject_ProjectId(projectId);
        List<Employees> employees = new ArrayList<>();
        for (EmployeesProjects ep : employeesProjects) {
            employees.add(employeesRepository.findById(ep.getEmployee().getEmployeeId()).orElse(null)); // 사원 정보 조회
        }
        return employees;
    }
// 8월 1일 적용 대기중
//    public void updateEmployeeSkillsForCompletedProject(Long projectId) {
//        List<EmployeesProjects> employeesProjects = employeesProjectsRepository.findByProjectId(projectId);
//
//        for (EmployeesProjects ep : employeesProjects) {
//            EmployeesSkills employeeSkill = employeesSkillsRepository.findByEmployeeIdAndSkillId(ep.getEmployee().getEmployeeId(), ep.getSkill().getSkillId())
//                    .orElseThrow(() -> new RuntimeException("Employee skill not found"));
//
//            double updatedSkillCareer = employeeSkill.getSkillCareer() + ep.getProjectDuration();
//            employeeSkill.setSkillCareer(updatedSkillCareer);
//
//            employeesSkillsRepository.save(employeeSkill);
//        }
//    }

}
