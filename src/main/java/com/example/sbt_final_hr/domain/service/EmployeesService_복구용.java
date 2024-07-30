package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesService_복구용 {

    private final EmployeesRepository employeesRepository;
    private final EmployeesSkillRepository employeesSkillRepository;
    private final SkillsRepository skillsRepository;


    @Autowired
    public EmployeesService_복구용(EmployeesRepository employeesRepository, EmployeesSkillRepository employeesSkillRepository, SkillsRepository skillsRepository) {
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


    public void createOrUpdateEmployee(EmployeesRequest dto) {
        Employees employee = new Employees();
        employee.setName(dto.getName());
        employee.setAddress(dto.getAddress());
        employee.setLatitude(dto.getLatitude());
        employee.setLongitude(dto.getLongitude());
        employee.setLastProjectEndDate(dto.getLastProjectEndDate());
        employee.setCurrentProjectEndDate(dto.getCurrentProjectEndDate());
        employee.setPreferredLanguage(Long.valueOf(dto.getPreferredLanguage()));
        employee.setPreferredProjectType(Long.valueOf(dto.getPreferredProjectType()));
        employee.setContactNumber(dto.getContactNumber());
        employee.setHireDate(dto.getHireDate());
        employee = employeesRepository.save(employee);


    }




























}
