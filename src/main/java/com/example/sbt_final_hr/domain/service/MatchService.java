package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final EmployeesRepository employeesRepository;
    private final ProjectsRepository projectsRepository;

    public MatchService(EmployeesRepository employeesRepository, ProjectsRepository projectsRepository) {
        this.employeesRepository = employeesRepository;
        this.projectsRepository = projectsRepository;
    }

//    public List<Employees> filterBySkills(Projects projects) {
//        List<Employees> Employees = employeesRepository.findAll();
//
//       return Employees;
//    }
//      아직 사원 별 스킬스택 테이블 없음

    public List<Employees> filterByProjectDates(List<Employees> employees, LocalDate projectStartDate) {
        return employees.stream()
                .filter(employee -> employee.getCurrentProjectEndDate() != null && employee.getCurrentProjectEndDate().isBefore(projectStartDate))
                .collect(Collectors.toList());
    }
}
