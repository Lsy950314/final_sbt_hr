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

    public MatchService(EmployeesRepository employeesRepository) {
        this.employeesRepository = employeesRepository;
    }

    public List<Employees> findEmployeesByProjectRequirements(Projects project) {
        return employeesRepository.findEmployeesByProjectRequirements(project.getProjectId());
    }

    public List<Employees> filterByProjectDates(List<Employees> employees, Projects project) {
        return employees.stream()
                .filter(employee -> employee.getCurrentProjectEndDate() == null || employee.getCurrentProjectEndDate().isBefore(project.getStartDate()))
                .collect(Collectors.toList());
    }

    // 세 조건을 모두 만족시키는 사원 필터링
    //3번째 조건 : 통근시간 기준으로 자르기 일단 90분
    public Map<Employees, Integer> filterByCommutingTime(List<Employees> employees, Projects projects) {
        // 1. 프로젝트 위치 정보 추출
        double projectLatitude = projects.getLatitude();
        double projectLongitude = projects.getLongitude();

        // 최대 통근시간 기준 (예: 90분)
        int maxCommutingTimeInMinutes = 90;

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
        // 1. HTTP 요청을 생성하는 팩토리 객체 생성
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(JSON_FACTORY.createJsonObjectParser()));

        // 2. Google Directions API의 요청 URL 설정
        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");
        url.put("origin", employeeLatitude + "," + employeeLongitude);
        url.put("destination", projectLatitude + "," + projectLongitude);
        url.put("modes", "transit");
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
        // 5. 경로를 찾을 수 없는 경우 최대 값을 반환
        return Integer.MAX_VALUE; // 경로를 찾을 수 없는 경우
    }

    public Map<Employees, Integer> filterEmployeesForProject(Projects project) {
        long startTime = System.currentTimeMillis();

        long step1StartTime = System.currentTimeMillis();
        List<Employees> filteredEmployeesByRequirements = findEmployeesByProjectRequirements(project);
        long step1EndTime = System.currentTimeMillis();

        System.out.println("조건1 : " + filteredEmployeesByRequirements);

        long step2StartTime = System.currentTimeMillis();
        List<Employees> availableEmployees = filterByProjectDates(filteredEmployeesByRequirements, project);
        long step2EndTime = System.currentTimeMillis();
        System.out.println("조건2 : " + availableEmployees);

        long step3StartTime = System.currentTimeMillis();
        Map<Employees, Integer> finalEmployees = filterByCommutingTime(availableEmployees, project);
        long step3EndTime = System.currentTimeMillis();
        System.out.println("조건3 : " + finalEmployees);


        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " milliseconds");
        System.out.println("Step 1 (filterEmployeesByProjectRequirements) took: " + (step1EndTime - step1StartTime) + " milliseconds");
        System.out.println("Step 2 (filterByProjectDates) took: " + (step2EndTime - step2StartTime) + " milliseconds");
        System.out.println("Step 3 (filterByCommutingTime) took: " + (step3EndTime - step3StartTime) + " milliseconds");

        return finalEmployees;
    }

    public void matchEmployeesProject (
            // 받아낼 파라미터
            // 어떤 projectReq 인지?
            //

    ) {

    }


}
