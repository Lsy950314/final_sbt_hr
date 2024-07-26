package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final EmployeesRepository employeesRepository;
    private final ProjectsRepository projectsRepository;
    private final ProjectRequirementsRepository projectRequirementsRepository;

    public MatchService(EmployeesRepository employeesRepository, ProjectsRepository projectsRepository, ProjectRequirementsRepository projectRequirementsRepository) {
        this.employeesRepository = employeesRepository;
        this.projectsRepository = projectsRepository;
        this.projectRequirementsRepository = projectRequirementsRepository;
    }

    // 첫 번째 조건: 프로젝트의 요구조건(스킬스택)에 부합하는 사원 필터링
    public List<Employees> filterEmployeesByProjectRequirements(Projects project) {
        List<Employees> allEmployees = employeesRepository.findAll();

        List<ProjectRequirements> projectRequirements = projectRequirementsRepository.findByProject_ProjectId(project.getProjectId());
//        System.out.println(projectRequirements);
        return allEmployees.stream()
                .filter(employee -> hasRequiredSkills(employee, projectRequirements))
                .collect(Collectors.toList());
    }

    // 사원이 프로젝트 요구사항을 충족하는지 확인하는 메서드
    private boolean hasRequiredSkills(Employees employee, List<ProjectRequirements> projectRequirements) {
        List<EmployeesSkill> employeeSkills = employee.getSkills();
//        System.out.println("Employee: " + employee.getName());
//        System.out.println("Employee Skills: " + employeeSkills);

        for (ProjectRequirements requirement : projectRequirements) {
//            System.out.println("Requirement: " + requirement.getSkill().getSkillName() + ", Required Experience: " + requirement.getRequiredExperience());

            boolean matches = employeeSkills.stream().anyMatch(skill -> {
                boolean skillNameMatches = skill.getSkill().getSkillName().equals(requirement.getSkill().getSkillName());
                boolean experienceMatches = skill.getSkillCareer() >= requirement.getRequiredExperience();

//                System.out.println("Checking skill: " + skill.getSkill().getSkillName() + ", Career: " + skill.getSkillCareer());
//                System.out.println("Skill Name Matches: " + skillNameMatches + ", Experience Matches: " + experienceMatches);

                return skillNameMatches && experienceMatches;
            });

            if (matches) {
//                System.out.println("Requirement met: " + requirement.getSkill().getSkillName());
                return true; // 하나라도 부합하면 true 반환
            }
        }

        return false; // 모든 요구조건에 부합하지 않으면 false 반환
    }


    public List<Employees> filterByProjectDates(List<Employees> employees, LocalDate projectStartDate) {
        return employees.stream()
                .filter(employee -> employee.getCurrentProjectEndDate() != null && employee.getCurrentProjectEndDate().isBefore(projectStartDate))
                .collect(Collectors.toList());
    }

}
