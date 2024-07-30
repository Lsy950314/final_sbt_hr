package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
import com.example.sbt_final_hr.domain.model.dto.EmployeesSkillRequest;
import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.EmployeesSkillRepository;
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

    //사진 넣기 시도중


    //Employee테이블, Employee_Skill테이블 모두에 튜플 삽입 가능한 create 메서드 추가 시도중.

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

    //오전 7월 30일 09:56
    public void createOrUpdateEmployee(EmployeesRequest dto, MultipartFile image) throws IOException {
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

        // Handle image upload
        if (!image.isEmpty()) {
            String imagePath = saveImage(image);
            employee.setImage(imagePath);
        }

        employee = employeesRepository.save(employee);

        if (dto.getEmployeesSkillRequests() != null) {
            for (EmployeesSkillRequest skillRequest : dto.getEmployeesSkillRequests()) {
                EmployeesSkill employeesSkill = new EmployeesSkill();
                employeesSkill.setEmployee(employee);
                employeesSkill.setSkill(skillsRepository.findById(skillRequest.getEmployeesSkillId()).orElse(null));
                employeesSkill.setSkillCareer(skillRequest.getSkillCareer());
                employeesSkillRepository.save(employeesSkill);
            }
        }
    }

//    public String saveImage(MultipartFile image) throws IOException {
//        String uploadsDir = "uploads/";
//        String originalFilename = image.getOriginalFilename();
//        String filePath = uploadsDir + UUID.randomUUID() + "_" + originalFilename;
//
//        File dir = new File(uploadsDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        Path path = Paths.get(filePath);
//        Files.write(path, image.getBytes());
//
//        return path.toString();
//    }

    public String saveImage(MultipartFile image) throws IOException {
        // Get the absolute path to the static folder
        ClassPathResource imgDirResource = new ClassPathResource("static/img/employees/");
        File imgDir = imgDirResource.getFile();

        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }

        String originalFilename = image.getOriginalFilename();
        String newFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(imgDir.getAbsolutePath(), newFilename);

        Files.write(filePath, image.getBytes());

        // Return the relative path to be saved in the database
        return "/img/employees/" + newFilename;
    }

}


























