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

    // s는 엔티티에 대한 별칭으로 임의로 사용한 것
    /**
     * SELECT s FROM Skills s WHERE s.id = :id
     */
    public Skills getSkillsById(Long id) {
        return skillsRepository.findById(id).orElse(null);
    }

    /**
     * SELECT s FROM Skills s
     */
    public List<Skills> getAllSkills() {
        return skillsRepository.findAll();
    }

    /**
     * INSERT INTO Skills (skillId, skillName) VALUES (?, ?)
     * ON DUPLICATE KEY UPDATE skillName = ?
     */
    public void saveSkills(SkillsRequest skillsRequest) {
        Skills skills = skillsRequest.toEntity();
        skillsRepository.save(skills);
    }

    /**
     * DELETE FROM Skills s WHERE s.id = :id
     */
    public void deleteSkills(Long id) {
        skillsRepository.deleteById(id);
    }

    // 커스텀 메서드 사용 예시
    public List<Skills> getSkillsByName(String name) {
        return skillsRepository.findBySkillName(name);
    }

    public List<Skills> getSkillsByNameLikeAndIdGreaterThan(String name, Long id) {
        return skillsRepository.findByNameLikeAndIdGreaterThan(name, id);
    }
}
