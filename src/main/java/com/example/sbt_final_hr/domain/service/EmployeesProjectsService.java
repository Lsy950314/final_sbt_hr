package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesProjects;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesProjectsRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
//import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmployeesProjectsService {
    private final EmployeesProjectsRepository employeesProjectsRepository;
    private final EmployeesRepository employeesRepository;
    private final EmployeesService employeesService;
    private final ProjectsService projectsService;
    private final ProjectRequirementsService projectRequirementsService;
    private final PlatformTransactionManager transactionManager;


    public EmployeesProjectsService(EmployeesProjectsRepository employeesProjectsRepository, EmployeesRepository employeesRepository, EmployeesService employeesService, ProjectsService projectsService, ProjectRequirementsService projectRequirementsService, PlatformTransactionManager transactionManager) {
        this.employeesProjectsRepository = employeesProjectsRepository;
        this.employeesRepository = employeesRepository;
        this.employeesService = employeesService;
        this.projectsService = projectsService;
        this.projectRequirementsService = projectRequirementsService;
        this.transactionManager = transactionManager;
    }

    public List<EmployeesProjects> getAllEmployeesProjects() {
        return employeesProjectsRepository.findAll();
    }

    public boolean deleteEmployeesProjects(Long employeeId, Long projectId) {
        EmployeesProjects employeesProjects = employeesProjectsRepository.findByEmployee_EmployeeIdAndProject_ProjectId(
                employeeId, projectId);
        if (employeesProjects != null) {
            employeesProjectsRepository.delete(employeesProjects);
            return true;
        }
        return false;
    }

    public boolean insertEmployeesProjects(Long employeeId, Long projectsId, Long projectRequirementId) {
        try {
            Projects projects = projectsService.getProjectById(projectsId);
            Double projectDuration = projects.getTotalProjectDurationInMonths();

            EmployeesProjectsRequest employeesProjectsRequest = new EmployeesProjectsRequest();

            employeesProjectsRequest.setEmployee(employeesService.findById(employeeId).get());
            employeesProjectsRequest.setProject(projects);
            employeesProjectsRequest.setRegistrationDate(LocalDateTime.now());
            employeesProjectsRequest.setStarPoint(null);
            employeesProjectsRequest.setProjectDuration(projectDuration);
            employeesProjectsRequest.setSkill(projectRequirementsService.getRequirementsById(projectRequirementId).getSkill());

            EmployeesProjects savedEntity = employeesProjectsRepository.save(employeesProjectsRequest.toEntity());

            return savedEntity != null;
        } catch (Exception e) {
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

    public void updateStarPointOfProjectParticipants(Long projectId, List<Map<String, Object>> projectParticipantsInfos) {
        for (Map<String, Object> participantInfo : projectParticipantsInfos) {
            Long employeeId = ((Number) participantInfo.get("employeeId")).longValue();
            Double starPoint = ((Number) participantInfo.get("starPoint")).doubleValue();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus status = transactionManager.getTransaction(def);

            try {
                employeesProjectsRepository.updateStarPointOfProjectParticipants(employeeId, projectId, starPoint);
                transactionManager.commit(status);
            } catch (Exception e) {
                transactionManager.rollback(status);
                throw e;
            }

        }
    }

    public List<EmployeesProjects> findByEmployeeId(long employeeId) {
        return employeesProjectsRepository.findByEmployee_EmployeeId(employeeId);
    }

}
