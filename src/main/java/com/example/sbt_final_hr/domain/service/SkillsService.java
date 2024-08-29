package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.SkillsRequest;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillsService {
    private final SkillsRepository skillsRepository;

    public SkillsService(SkillsRepository skillsRepository) {
        this.skillsRepository = skillsRepository;
    }


    public Skills getSkillsById(Long id) {
        return skillsRepository.findById(id).orElse(null);
    }

    public List<Skills> getAllSkills() {
        return skillsRepository.findAll();
    }

    public void saveSkills(SkillsRequest skillsRequest) {
        Skills skills = skillsRequest.toEntity();
        skillsRepository.save(skills);
    }

    public void deleteSkills(Long id) {
        skillsRepository.deleteById(id);
    }

    public Optional<Skills> findById(Long id) {
        return skillsRepository.findById(id);
    }


}
