package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final EmployeesRepository employeesRepository;

    public MatchService(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }

    public List<Employees> findEmployeesByProjectRequirements(Projects project) {
        return employeesRepository.findEmployeesByProjectRequirements(project.getProjectId());
    }

    public List<Employees> filterByProjectDates(List<Employees> employees, Projects project) {
        return employees.stream()
                .filter(employee -> employee.getCurrentProjectEndDate() != null && employee.getCurrentProjectEndDate().isBefore(project.getStartDate()))
                .collect(Collectors.toList());
    }

    // 두 조건을 모두 만족시키는 사원 필터링
    public List<Employees> filterEmployeesForProject(Projects project) {
        long startTime = System.currentTimeMillis();

        long step1StartTime = System.currentTimeMillis();
        List<Employees> filteredEmployeesByRequirements = findEmployeesByProjectRequirements(project);
        long step1EndTime = System.currentTimeMillis();

        long step2StartTime = System.currentTimeMillis();
        List<Employees> availableEmployees = filterByProjectDates(filteredEmployeesByRequirements, project);
        long step2EndTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " milliseconds");
        System.out.println("Step 1 (filterEmployeesByProjectRequirements) took: " + (step1EndTime - step1StartTime) + " milliseconds");
        System.out.println("Step 2 (filterByProjectDates) took: " + (step2EndTime - step2StartTime) + " milliseconds");

        return availableEmployees;
    }


}
