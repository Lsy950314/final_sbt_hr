package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;

import java.util.List;

public class SkillsService {
    private final SkillsRepository skillsRepository;

    public SkillsService(SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }

    public List<Skills> getAllSkills(){
        return skillsRepository.findAll();
    }
}
