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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
//    public void saveEmployeeWithImage(EmployeesRequest employeeRequest, MultipartFile file) {
//        // 파일이 저장될 경로 설정
//        var path = System.getProperty("user.dir") + "/src/main/resources/static/img/employees/";
//        var pathCheck = new File(path);
//        if (!pathCheck.exists()) {
//            pathCheck.mkdirs();
//        }
//        // 서버 경로 설정
//        var serverPath = path.split("static/")[1];
//        var uuid = UUID.randomUUID();
//        // 파일명 재정의
//        var fileName = !file.getOriginalFilename().isEmpty() ? uuid + "." + file.getOriginalFilename().split("\\.")[1] : null;
//        if (fileName != null) {
//            // 저장할 파일 생성
//            var saveTo = new File(path + fileName);
//            // 서버 경로 업데이트
//            serverPath = serverPath + fileName;
//
//            var existingEmployee = this.getEmployeeById(employeeRequest.getId());
//            if (existingEmployee != null && existingEmployee.getImage() != null) {
//                var existingFile = new File(System.getProperty("user.dir") + "/src/main/resources/static/" + existingEmployee.getImage());
//                if (existingFile.exists()) {
//                    existingFile.delete();
//                }
//            }
//
//            try {
//                // 업로드된 파일을 설정된 경로에 저장
//                file.transferTo(saveTo);
//                employeesRepository.save(employeeRequest.toEntity(serverPath));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        } else {
//            employeesRepository.save(employeeRequest.toEntity(employeesRepository.findById(employeeRequest.getId()).orElseThrow().getImage()));
//        }
//    }



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

























}
