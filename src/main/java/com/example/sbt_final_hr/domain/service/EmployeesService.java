package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final EmployeesSkillRepository employeesSkillRepository;
    private final SkillsRepository skillsRepository;


    @Autowired
    public EmployeesService(EmployeesRepository employeesRepository, EmployeesSkillRepository employeesSkillRepository, SkillsRepository skillsRepository) {
        this.employeesRepository = employeesRepository;
        this.employeesSkillRepository = employeesSkillRepository;
        this.skillsRepository = skillsRepository;
    }

    public Employees save(Employees employee) {
        return employeesRepository.save(employee);
    }

    public void saveEmployeeSkill(EmployeesSkill employeesSkill) {
        employeesSkillRepository.save(employeesSkill);
    }

    public List<Employees> findAll() {
        return employeesRepository.findAll();
    }

    public List<Employees> findByName(String name) {
        return employeesRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<Employees> findById(Long id) {
        return employeesRepository.findById(id);
    }

    public void deleteById(Long id) {
        employeesRepository.deleteById(id);
    }



    //Employee테이블, Employee_Skill테이블 모두에 튜플 삽입 가능한 create 메서드 추가 시도중.

    public void createOrUpdateEmployee(EmployeesRequest dto) {
        Employees employee = new Employees();
        employee.setName(dto.getName());
        employee.setAddress(dto.getAddress());
        employee.setLatitude(dto.getLatitude());
        employee.setLongitude(dto.getLongitude());
        employee.setLastProjectEndDate(dto.getLastProjectEndDate());
        employee.setCurrentProjectEndDate(dto.getCurrentProjectEndDate());
        employee.setPreferredLanguage(dto.getPreferredLanguage());
        employee.setPreferredProjectType(dto.getPreferredProjectType());
        employee.setContactNumber(dto.getContactNumber());
        employee.setHireDate(dto.getHireDate());
        employee = employeesRepository.save(employee);

//        for (EmployeesRequest.ProgrammingExperience experience : dto.getSkills()) {
//            Skills skill = skillsRepository.findById(experience.getSkillLanguage()).orElseThrow(() -> new RuntimeException("Skill not found"));
//            EmployeesSkill employeesSkill = new EmployeesSkill();
//            employeesSkill.setEmployee(employee);
//            employeesSkill.setSkill(skill);
//            employeesSkill.setSkillCareer(experience.getSkillCareer());
//            employeesSkillRepository.save(employeesSkill);
//        }


        //Employee테이블, Employee_Skill테이블 모두에 튜플 삽입 가능한 create 메서드 추가 시도중.



    }

























}
