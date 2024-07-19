package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Long> {
    // 스킬 이름으로 검색
    List<Skills> findBySkillName(String skillName);

    // JPQL을 사용한 복잡한 쿼리 예시
    @Query("SELECT s FROM Skills s WHERE s.skillName LIKE %:name% AND s.skillId > :id")
    List<Skills> findByNameLikeAndIdGreaterThan(@Param("name") String name, @Param("id") Long id);
}
