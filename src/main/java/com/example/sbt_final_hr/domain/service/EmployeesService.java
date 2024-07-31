package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.EmployeesRequest;
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
//  엔티티 고쳐서 아마도 안써도 될듯
//    public Employees save(Employees employee) {
//        if (employee.getEmployeeId() != null) {
//            Optional<Employees> existingEmployee = employeesRepository.findById(employee.getEmployeeId());
//            if (existingEmployee.isPresent()) {
//                // 기존 엔티티가 있는 경우 업데이트
//                Employees updatedEmployee = existingEmployee.get();
//                updatedEmployee.setName(employee.getName());
//                updatedEmployee.setAddress(employee.getAddress());
//                updatedEmployee.setLatitude(employee.getLatitude());
//                updatedEmployee.setLongitude(employee.getLongitude());
//                updatedEmployee.setLastProjectEndDate(employee.getLastProjectEndDate());
//                updatedEmployee.setCurrentProjectEndDate(employee.getCurrentProjectEndDate());
//                updatedEmployee.setPreferredLanguage(employee.getPreferredLanguage());
//                updatedEmployee.setPreferredProjectType(employee.getPreferredProjectType());
//                updatedEmployee.setContactNumber(employee.getContactNumber());
//                updatedEmployee.setHireDate(employee.getHireDate());
//                updatedEmployee.setImage(employee.getImage());
//                return employeesRepository.save(updatedEmployee);
//            }
//        }
//        // 새로운 엔티티인 경우 저장
//        return employeesRepository.save(employee);
//    }
//
    public Employees save(Employees employee) {
        return employeesRepository.save(employee);
    }

//    public Employees save(int id, Employees employee) {
//        return employeesRepository.save(employee);
//    }

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






















}
