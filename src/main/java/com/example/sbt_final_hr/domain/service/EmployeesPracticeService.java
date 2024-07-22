package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.EmployeesPractice;
import com.example.sbt_final_hr.domain.repository.EmployeesPracticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeesPracticeService {

    private final EmployeesPracticeRepository employeesPracticeRepository;

    public EmployeesPracticeService(EmployeesPracticeRepository employeesPracticeRepository) {
        this.employeesPracticeRepository = employeesPracticeRepository;
    }

    public EmployeesPractice save(EmployeesPractice employee) {
        return employeesPracticeRepository.save(employee);
    }

    public List<EmployeesPractice> findAll() {
        return employeesPracticeRepository.findAll();
    }

    public List<EmployeesPractice> findByName(String name) {
        return employeesPracticeRepository.findByNameContainingIgnoreCase(name);
    }


    public Optional<EmployeesPractice> findById(Long id) {
        return employeesPracticeRepository.findById(id);
    }

    public void deleteById(Long id) {
        employeesPracticeRepository.deleteById(id);
    }


}
