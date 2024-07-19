package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees_practice;
import com.example.sbt_final_hr.domain.repository.Employees_practiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Employees_practiceService {
    @Autowired
    private Employees_practiceRepository employees_practiceRepository;
    //read
    public List<Employees_practice> findAll() { return employees_practiceRepository.findAll(); }

}
