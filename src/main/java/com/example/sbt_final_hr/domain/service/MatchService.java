package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final EmployeesRepository employeesRepository;
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final ProjectRequirementsService projectRequirementsService;
    private final EmployeesProjectsService employeesProjectsService;
    private final EmployeesService employeesService;
    private final ProjectsService projectsService;

    public MatchService(EmployeesRepository employeesRepository, ProjectRequirementsService projectRequirementsService, EmployeesProjectsService employeesProjectsService, EmployeesService employeesService, ProjectsService projectsService) {
        this.employeesRepository = employeesRepository;
        this.projectRequirementsService = projectRequirementsService;
        this.employeesProjectsService = employeesProjectsService;
        this.employeesService = employeesService;
        this.projectsService = projectsService;
    }

    public List<Employees> findEmployeesByProjectRequirements(Projects project) {
        return employeesRepository.findEmployeesByProjectRequirements(project.getProjectId());
    }

    public boolean matchEmployeeToProject(Long projectId, Long employeeId, Long projectRequirementsId) {
        if (projectRequirementsService.updateFulfilledCount(projectRequirementsId)) {
            if (employeesProjectsService.insertEmployeesProjects(employeeId, projectId, projectRequirementsId)) {
                if (employeesService.updateAllocationTo(employeeId, 1)) {
                    employeesService.updateEndDates(employeeId, projectId);
                }
            }
            // 모든 요구인원이 충족됐다면 projects 의 status 를 1로 바꿔주는 로직
            if (projectRequirementsService.checkFulfilledCount(projectId)) {
                projectsService.updateStatusTo(projectId, 1);
            }
            return true;
        } else {
            return false;
        }

    }

    public boolean matchCancel(Long projectId, Long employeeId, Long projectRequirementsId) {
        // 배정 로직을 반대로
        // 충족인원 -1
        if (projectRequirementsService.decreaseFulfilledCount(projectRequirementsId) &&
                employeesProjectsService.deleteEmployeesProjects(employeeId, projectId) &&
                employeesService.restoreEndDates(employeeId) &&
                employeesService.updateAllocationTo(employeeId, -1)
        ) {// 모든 로직이 성공하면 프로젝트의 status 값 -1로 바꿔주기
            projectsService.updateStatusTo(projectId, -1);
            return true;
        }
        return false;

    }

    // 세 조건을 모두 만족시키는 사원 필터링
    // 3번째 조건 : 통근시간 기준으로 자르기 일단 90분
    @Value("${commuting.max-time-in-minutes}")
    private int maxCommutingTimeInMinutes;

    public Map<Employees, Integer> filterByCommutingTime(List<Employees> employees, Projects projects) {
        // 1. 프로젝트 위치 정보 추출
        double projectLatitude = projects.getLatitude();
        double projectLongitude = projects.getLongitude();

        // 통근 시간 캐시
        Map<String, Integer> transitTimeCache = new HashMap<>();

        List<CompletableFuture<AbstractMap.SimpleEntry<Employees, Integer>>> futures = employees.stream()
                .map(employee -> CompletableFuture.supplyAsync(() -> {
                    double employeeLatitude = employee.getLatitude();
                    double employeeLongitude = employee.getLongitude();
                    String cacheKey = employeeLatitude + "," + employeeLongitude + "->" + projectLatitude + "," + projectLongitude;

                    // 캐시에서 통근 시간 조회
                    if (transitTimeCache.containsKey(cacheKey)) {
                        return new AbstractMap.SimpleEntry<>(employee, transitTimeCache.get(cacheKey));
                    }

                    try {
                        int transitTimeInMinutes = getTransitTimeInMinutes(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude);
                        transitTimeCache.put(cacheKey, transitTimeInMinutes); // 캐시에 저장
                        return new AbstractMap.SimpleEntry<>(employee, transitTimeInMinutes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new AbstractMap.SimpleEntry<>(employee, -1); // 에러 발생 시 -1로 설정
                    }
                })).collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(entry -> entry.getValue() <= maxCommutingTimeInMinutes && entry.getValue() != -1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Google Directions API를 사용하여 두 좌표 간의 대중교통 경로 시간을 계산합니다.
    private int getTransitTimeInMinutes(double employeeLatitude, double employeeLongitude, double projectLatitude, double projectLongitude) throws IOException {
        // 1차 시도
        int duration = getDurationWithMode(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude, "modes");

        // 경로를 찾을 수 없는 경우 (Integer.MAX_VALUE 반환), 2차 시도
        if (duration == Integer.MAX_VALUE) {
            duration = getDurationWithMode(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude, "mode");
        }

        return duration;
    }

    private int getDurationWithMode(double employeeLatitude, double employeeLongitude, double projectLatitude, double projectLongitude, String modeKey) throws IOException {
        // 1. HTTP 요청을 생성하는 팩토리 객체 생성
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(JSON_FACTORY.createJsonObjectParser()));

        // 2. Google Directions API의 요청 URL 설정
        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");
        url.put("origin", employeeLatitude + "," + employeeLongitude);
        url.put("destination", projectLatitude + "," + projectLongitude);
        url.put(modeKey, "transit");  // modes 또는 mode 키를 사용
        url.put("key", "AIzaSyD66g27MXhvDFVCYdrAsu60XM91URf2UFY"); // 여기에 실제 API 키를 입력하세요.

        // 3. HTTP GET 요청을 실행하고 응답을 받아옴
        HttpResponse response = requestFactory.buildGetRequest(url).execute();
        Map<String, Object> directionsResponse = response.parseAs(Map.class);

        // 4. 응답에서 경로 정보를 추출
        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");

        if (routes != null && !routes.isEmpty()) {
            List<Map<String, Object>> legs = (List<Map<String, Object>>) routes.get(0).get("legs");

            if (legs != null && !legs.isEmpty()) {
                Map<String, Object> duration = (Map<String, Object>) legs.get(0).get("duration");

                if (duration != null) {
                    return ((Number) duration.get("value")).intValue() / 60; // 초 단위를 분 단위로 변환
                }
            }
        }
        // 경로를 찾을 수 없는 경우 최대 값을 반환
        return Integer.MAX_VALUE;
    }

    // 현 상태: 2개의 STEP 합침, ALLOCATION 이 -1인 사람들 중 요구조건 맞는 사원만 걸러낸 후 통근시간 판단
    public Map<Employees, Integer> filterEmployeesForProject(Projects project) {
        long startTime = System.currentTimeMillis();

        long step1StartTime = System.currentTimeMillis();
        List<Employees> filteredEmployeesByRequirements = findEmployeesByProjectRequirements(project);
        long step1EndTime = System.currentTimeMillis();
        System.out.println("요구 스킬을 충족하는 사원들 : " + filteredEmployeesByRequirements);
        System.out.println("해당 사원 수 : " + filteredEmployeesByRequirements.size());

        long step2StartTime = System.currentTimeMillis();
        Map<Employees, Integer> finalEmployees = filterByCommutingTime(filteredEmployeesByRequirements, project);
        long step2EndTime = System.currentTimeMillis();
        System.out.println("통근 시간 조건을 만족하는 사원들 : " + finalEmployees);
        System.out.println("해당 사원 수 : " + finalEmployees.size());

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " milliseconds");
        System.out.println("Step 1 (filterEmployeesByProjectRequirements) took: " + (step1EndTime - step1StartTime) + " milliseconds");
        System.out.println("Step 2 (filterByCommutingTime) took: " + (step2EndTime - step2StartTime) + " milliseconds");

        return finalEmployees;
    }
}
