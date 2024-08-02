package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    private final EmployeesRepository employeesRepository;
    private final EmployeesService employeesService;
    private final ProjectsService projectsService;
    private final ProjectRequirementsService projectRequirementsService;

    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository, EmployeesRepository employeesRepository, EmployeesService employeesService, ProjectsService projectsService, ProjectRequirementsService projectRequirementsService) {
        this.employeesProjectsRepository = employeesProjectsRepository;
        this.employeesRepository = employeesRepository;
        this.employeesService = employeesService;
        this.projectsService = projectsService;
        this.projectRequirementsService = projectRequirementsService;
    }

    public List<EmployeesProjects> getAllEmployeesProjects() {
        return employeesProjectsRepository.findAll();
    }

    public boolean insertEmployeesProjects(Long employeeId, Long projectsId, Long projectRequirementId) {
        try {
            Projects projects = projectsService.getProjectById(projectsId);
            Double projectDuration = projects.getTotalProjectDurationInMonths();
            System.out.println(projectDuration);

            EmployeesProjectsRequest employeesProjectsRequest = new EmployeesProjectsRequest();

            employeesProjectsRequest.setEmployee(employeesService.findById(employeeId).get());
            employeesProjectsRequest.setProject(projects);
            employeesProjectsRequest.setRegistrationDate(LocalDateTime.now());
            employeesProjectsRequest.setStarPoint(null);
            employeesProjectsRequest.setProjectDuration(projectDuration);
            employeesProjectsRequest.setSkill(projectRequirementsService.getRequirementsById(projectRequirementId).getSkill());

            EmployeesProjects savedEntity = employeesProjectsRepository.save(employeesProjectsRequest.toEntity());

            // save가 null을 반환하지 않으면 성공으로 간주하고 true 반환
            return savedEntity != null;
        } catch (Exception e) {
            // 예외가 발생하면 false 반환
            e.printStackTrace();
            return false;
        }
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
