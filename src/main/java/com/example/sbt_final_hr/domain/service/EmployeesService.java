package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.entity.*;
import com.example.sbt_final_hr.domain.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeesService {

    private final EmployeesRepository employeesRepository;
    private final EmployeesSkillRepository employeesSkillRepository;
    private final SkillsRepository skillsRepository;
    private final ProjectsRepository projectsRepository;
    private final EmployeesProjectsRepository employeesProjectsRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        employees.setPreviousProjectEndDate(employees.getLastProjectEndDate());
        employees.setCurrentProjectEndDate(projects.getEndDate());
        employees.setLastProjectEndDate(null);
        employeesRepository.save(employees);
    }

    public boolean restoreEndDates(Long employeeId) {
        Employees employees = employeesRepository.findById(employeeId).orElseThrow(RuntimeException::new);
        if (employees.getPreviousProjectEndDate() != null) {
            employees.setLastProjectEndDate(employees.getPreviousProjectEndDate());
        } else {
            employees.setLastProjectEndDate(null);
        }
        employees.setPreviousProjectEndDate(null);
        employees.setCurrentProjectEndDate(null);
        employeesRepository.save(employees);
        return true;
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

    public String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

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

    public Employees findByEmployeeId(Long employeeId) {
        return employeesRepository.findById(employeeId).orElse(null);
    }



    @Transactional
    public boolean updateAllocationTo(long id, int num) {
        System.out.println(id);
        System.out.println(num);
        if (employeesRepository.updateAllocationTo(id, num)==1){
            employeesRepository.flush();
            entityManager.clear();
            // 엔티티를 다시 로드하여 상태가 업데이트되었는지 확인
            Employees employee = employeesRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
            System.out.println("Updated allocation: " + employee.getAllocation());
            return true;
        } else {
            return false;
        }
       
    }

    public List<EmployeesRequest> findAllEmployeesSummary() {
        return employeesRepository.findAllEmployeesSummary();
    }


    public Map<String, Integer> getCountEmployees(){
        Map<String, Integer> countEmployees = new HashMap<>();

        int totalEmployees = employeesRepository.countAllEmployees();
        int assignedEmployees = employeesRepository.countAssignedEmployees();
        int unassignedEmployees = employeesRepository.countUnassignedEmployees();

        countEmployees.put("totalEmployees", totalEmployees);
        countEmployees.put("assignedEmployees", assignedEmployees);
        countEmployees.put("unassignedEmployees", unassignedEmployees);

        return countEmployees;
    }

    public boolean isallocation1(long employeeId) {
        int allocationbyEmployeeId = employeesRepository.findAllocationByEmployeeId(employeeId);
        return allocationbyEmployeeId == 1;
    }

}
