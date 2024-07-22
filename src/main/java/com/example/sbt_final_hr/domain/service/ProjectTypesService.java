package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.ProjectTypes;
import com.example.sbt_final_hr.domain.repository.ProjectTypesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTypesService {
   private final ProjectTypesRepository projectTypesRepository;

    public ProjectTypesService(ProjectTypesRepository projectTypesRepository) {
        this.projectTypesRepository = projectTypesRepository;
    }

    public List<ProjectTypes> getAllProjectTypes() {
        return projectTypesRepository.findAll();
    }
}
