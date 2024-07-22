package com.example.sbt_final_hr.domain.repository;

import com.example.sbt_final_hr.domain.model.entity.EmployeesPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeesPracticeRepository extends JpaRepository<EmployeesPractice, Long> {
    // 추가적인 쿼리 메서드를 정의할 수 있습니다.
    List<EmployeesPractice> findByNameContainingIgnoreCase(String name);
}
