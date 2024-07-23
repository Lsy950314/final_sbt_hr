package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;

    public EmployeesService(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }

    public Employees save(Employees employee) {
        return employeesRepository.save(employee);
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


}
