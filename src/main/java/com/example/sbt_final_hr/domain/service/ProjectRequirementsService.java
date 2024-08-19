package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.dto.ProjectRequirementsRequest;
import com.example.sbt_final_hr.domain.model.dto.ProjectsRequest;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.model.entity.Skills;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.example.sbt_final_hr.domain.repository.SkillsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectRequirementsService {
    private final ProjectRequirementsRepository projectRequirementsRepository;
    private final ProjectsRepository projectsRepository;
    private final SkillsRepository skillsRepository;

    public ProjectRequirementsService(ProjectRequirementsRepository projectRequirementsRepository, ProjectsRepository projectsRepository, SkillsRepository skillsRepository) {
        this.projectRequirementsRepository = projectRequirementsRepository;
        this.projectsRepository = projectsRepository;
        this.skillsRepository = skillsRepository;
    }

    public List<ProjectRequirements> findByProjectId(Long projectId) {
        return projectRequirementsRepository.findByProject_ProjectId(projectId);
    }

    public ProjectRequirements getRequirementsById(Long id) {
        return projectRequirementsRepository.findById(id).orElseThrow();
    }

//    public List<ProjectRequirements> getRequirementsByProjectId(Long projectId) {
//        return projectRequirementsRepository.findByProject_ProjectId(projectId);
//    }

    //인원 요구 충족된 경우 List<ProjectRequirements> 목록의 밑으로 가는 코드 8월 19일 10:15 민승현 작성
    public List<ProjectRequirements> getRequirementsByProjectId(Long projectId) {
        // DB에서 ProjectRequirements 리스트를 가져옴
        List<ProjectRequirements> requirements = projectRequirementsRepository.findByProject_ProjectId(projectId);

        // fulfilledCount와 requiredCount가 같은 항목들은 뒤로 보내도록 정렬
        return requirements.stream()
                .sorted((req1, req2) -> {
                    boolean isReq1Fulfilled = req1.getFulfilledCount() == req1.getRequiredCount();
                    boolean isReq2Fulfilled = req2.getFulfilledCount() == req2.getRequiredCount();

                    // fulfilledCount와 requiredCount가 같은 항목은 뒤로 보내기 위해 정렬 기준 설정
                    if (isReq1Fulfilled && !isReq2Fulfilled) {
                        return 1; // req1이 req2보다 뒤에 위치하도록
                    } else if (!isReq1Fulfilled && isReq2Fulfilled) {
                        return -1; // req1이 req2보다 앞에 위치하도록
                    } else {
                        return 0; // 순서를 변경하지 않음
                    }
                })
                .collect(Collectors.toList());
    }
    //인원 요구 충족된 경우 목록의 밑으로 가는 코드 8월 19일 10:15 민승현 작성


    public void createProjectRequirements(ProjectRequirements projectRequirements) {
        projectRequirementsRepository.save(projectRequirements);
    }

    public boolean updateFulfilledCount(Long projectRequirementId) {
        ProjectRequirements projectRequirements = projectRequirementsRepository.findById(projectRequirementId).orElseThrow(() -> new RuntimeException("Project requirements not found"));
//        System.out.println(projectRequirements.getRequiredCount());
//        System.out.println(projectRequirements.getFulfilledCount());
        if (projectRequirements.getRequiredCount() != projectRequirements.getFulfilledCount()) {
//            System.out.println(projectRequirements.getRequiredCount());
            projectRequirements.setFulfilledCount(projectRequirements.getFulfilledCount() + 1);
            System.out.println("요구사항 인원 추가 성공");
            return true;
        } else {
            throw new RuntimeException("이미 충족된 요구사항입니다. 배정된 사원을 취소한 뒤 새로운 사원을 배정하세요");
        }
    }

    public boolean decreaseFulfilledCount(Long projectRequirementsId) {
        ProjectRequirements pr = projectRequirementsRepository.findById(projectRequirementsId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        int fc = pr.getFulfilledCount();
        if (fc > 0) {
            pr.setFulfilledCount(fc - 1);
            return true;
        }
        return false;
    }

    public boolean checkFulfilledCount(Long projectId) {
        List<ProjectRequirements> prList = this.getRequirementsByProjectId(projectId);
        for (ProjectRequirements pr : prList) {
            if (pr.getFulfilledCount() != pr.getRequiredCount()) {
                return false;
            }
        }
        return true;
    }

    public void deleteByProjectId(Long projectId) {
        List<ProjectRequirements> requirements = projectRequirementsRepository.findByProject_ProjectId(projectId);
        projectRequirementsRepository.deleteAll(requirements);
    }
}

