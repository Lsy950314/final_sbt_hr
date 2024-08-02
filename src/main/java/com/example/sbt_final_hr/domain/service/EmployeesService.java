package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final EmployeesSkillRepository employeesSkillRepository;
    private final SkillsRepository skillsRepository;
    private final ProjectsRepository projectsRepository;


    @Autowired
    public EmployeesService(EmployeesRepository employeesRepository, EmployeesSkillRepository employeesSkillRepository, SkillsRepository skillsRepository, ProjectsRepository projectsRepository) {
        this.employeesRepository = employeesRepository;
        this.employeesSkillRepository = employeesSkillRepository;
        this.skillsRepository = skillsRepository;
        this.projectsRepository = projectsRepository;
    }

    public Employees save(Employees employee) {
        return employeesRepository.save(employee);
    }

    public void updateEndDates(Long employeeId, Long projectId) {
        Projects projects = projectsRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Employees employees = employeesRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        employees.setCurrentProjectEndDate(projects.getEndDate());
        employees.setLastProjectEndDate(null);
        employeesRepository.save(employees);
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

//13시 20분 시도중. chat gpt
public String saveImage(MultipartFile image) throws IOException {
    // Get the absolute path to the static folder
    ClassPathResource imgDirResource = new ClassPathResource("static/img/employees/");
    File imgDir = imgDirResource.getFile();

    if (!imgDir.exists()) {
        if (!imgDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + imgDir.getAbsolutePath());
        }
    }

    String originalFilename = image.getOriginalFilename();
    String newFilename = UUID.randomUUID() + "_" + originalFilename;
    Path filePath = Paths.get(imgDir.getAbsolutePath(), newFilename);

    System.out.println(filePath + " " + newFilename);

    Files.write(filePath, image.getBytes());

    // Return the relative path to be saved in the database
    return "/img/employees/" + newFilename;
}

// 사원 비교를 위해 만듬
    public Employees findEmployeeById(Long employeeId) {
        return null;
    }
}
