package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.SkillsRequest;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillsService {
    private final SkillsRepository skillsRepository;

    public SkillsService(SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }

    public List<Skills> getAllSkills(){
        return skillsRepository.findAll();
    }

    public void saveSkills(SkillsRequest skillsRequest) {
        Skills skills = skillsRequest.toEntity();
        skillsRepository.save(skills);
    }
}
