package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.*;
import com.example.sbt_final_hr.domain.repository.*;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final EmployeesSkillRepository employeesSkillRepository;
    private final SkillsRepository skillsRepository;
    private final ProjectsRepository projectsRepository;
    private final EmployeesProjectsRepository employeesProjectsRepository;


    @Autowired
    public EmployeesService(EmployeesRepository employeesRepository, EmployeesSkillRepository employeesSkillRepository, SkillsRepository skillsRepository, ProjectsRepository projectsRepository, EmployeesProjectsRepository employeesProjectsRepository) {
        this.employeesRepository = employeesRepository;
        this.employeesSkillRepository = employeesSkillRepository;
        this.skillsRepository = skillsRepository;
        this.projectsRepository = projectsRepository;
        this.employeesProjectsRepository = employeesProjectsRepository;
    }

    public Employees save(Employees employee) {
        return employeesRepository.save(employee);
    }

    public void updateEndDates(Long employeeId, Long projectId) {
        Projects projects = projectsRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        Employees employees = employeesRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        // 취소 로직을 대비해서 값을 기록해 둠
        employees.setPreviousProjectEndDate(employees.getLastProjectEndDate());
        // 최신화
        employees.setCurrentProjectEndDate(projects.getEndDate());
        // 프로젝트에 참여 중인 상태가 될 것이므로 대기기간 계산 로직을 위한 최근 프로젝트 종료일은 null 로 만들어 줌
        employees.setLastProjectEndDate(null);
        employeesRepository.save(employees);
    }

    public boolean restoreEndDates(Long employeeId) {
        Employees employees = employeesRepository.findById(employeeId).orElseThrow(RuntimeException::new);
        if (employees.getPreviousProjectEndDate() != null) {
            employees.setLastProjectEndDate(employees.getPreviousProjectEndDate());
            employees.setPreviousProjectEndDate(null);
            employeesRepository.save(employees);
            return true;
        }
        return false;
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

    @Transactional
    public void updateStarPointAverageOfProjectParticipants(long employeeId) {
        Double averageStarPoint = employeesProjectsRepository.calculateAverageStarPointByEmployeeId(employeeId);
        employeesRepository.updateStarPointAverage(employeeId, averageStarPoint);
    }

    @Transactional
    public void updateProjectEndDateOfProjectParticipants(long employeeId) {
        employeesRepository.updateProjectEndDates(employeeId);
    }

    public List<Employees> findByProjectId(Long projectId) {
        List<EmployeesProjects> employeesProjects = employeesProjectsRepository.findByProject_ProjectId(projectId);
        return employeesProjects.stream().map(EmployeesProjects::getEmployee).collect(Collectors.toList());
    }




}
