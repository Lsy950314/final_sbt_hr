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
import java.util.List;
import java.util.Map;
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
    public List<Employees> filterByCommutingTime(List<Employees> employees ,Projects projects) {
        //1.프로젝트 위치 정보 추출
        double projectLatitude = projects.getLatitude();
        double projectLongitude = projects.getLongitude();

        //1-1 프로젝트 위치 정보가 찍히는지 확인. 예시인 오사카 역은 좌표가 34.700648,135.4933488 나와야 한다.
        System.out.println("filterByCommutingTime메서드의 1.프로젝트 위치 정보 찍히는지 확인");
        System.out.println(projectLatitude);
        System.out.println(projectLongitude);
        //잘 나옴


        //최대 통근시간 기준 90분, 100분, 110분
        int maxCommutingTimeInMinutes  = 90;

        return employees.stream()
                .filter(employee -> {
                    double employeeLatitude = employee.getLatitude();
                    double employeeLongitude = employee.getLongitude();
                    System.out.println("각 직원의 좌표 정보 출력");
                    System.out.println("Employee ID: " + employee.getEmployeeId());
                    System.out.println("Employee Latitude: " + employeeLatitude);
                    System.out.println("Employee Longitude: " + employeeLongitude);
                    //잘 나옴


                    try {
                        int transitTimeInMinutes = getTransitTimeInMinutes(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude);
                        System.out.println("통근 시간 정보 출력");
                        System.out.println("Transit Time for Employee ID " + employee.getEmployeeId() + ": " + transitTimeInMinutes + " minutes");
                        //잘 나옴
                        return transitTimeInMinutes <= maxCommutingTimeInMinutes;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());

    }

    //Google Directions API를 사용하여 두 좌표 간의 대중교통 경로 시간을 계산합니다.
    private int getTransitTimeInMinutes(double employeeLatitude, double employeeLongitude, double projectLatitude, double projectLongitude) throws IOException {
        System.out.println("getTransitTimeInMinutes메서드에 매개변수가 잘 전달되었는지 확인중");
        System.out.println("Employee Latitude: " + employeeLatitude);
        System.out.println("Employee Longitude: " + employeeLongitude);
        System.out.println("Project Latitude: " + projectLatitude);
        System.out.println("Project Longitude: " + projectLongitude);
        //잘 나옴



        //1. HTTP 요청을 생성하는 팩토리 객체 생성
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(JSON_FACTORY.createJsonObjectParser()));
        //??Map<String, Object> directionsResponse = response.parseAs(Map.class, JSON_FACTORY); // GsonFactory를 사용하여 JSON 응답을 파싱
        // 2. Google Directions API의 요청 URL 설정
        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");


        url.put("origin", employeeLatitude + "," + employeeLongitude);
        url.put("destination", projectLatitude + "," + projectLongitude);
        //url.put("mode", "transit"); //대중교통 모드
        url.put("modes", "transit"); //걷기 모드

        url.put("key", "AIzaSyD66g27MXhvDFVCYdrAsu60XM91URf2UFY");
        // 3. HTTP GET 요청을 실행하고 응답을 받아옴
        HttpResponse response = requestFactory.buildGetRequest(url).execute();
        int statusCode = response.getStatusCode();
        System.out.println("HTTP Status Code: " + statusCode);
        if (statusCode != 200) {
            System.out.println("Error: " + response.parseAsString());
            return Integer.MAX_VALUE;
        }
        Map<String, Object> directionsResponse = response.parseAs(Map.class);
        System.out.println(" HTTP GET 요청에 대한 응답을 출력");
        System.out.println(directionsResponse);

        //https://maps.googleapis.com/maps/api/directions/json?origin=34.6885789,135.534485&destination=34.7024854,135.4959506&mode=walking&key=AIzaSyD66g27MXhvDFVCYdrAsu60XM91URf2UFY
        //저렇게 하면 걷는모드로 하면 나오긴 나오는데 에휴
        //https://maps.googleapis.com/maps/api/directions/json?origin=35.6703,139.7715&destination=34.7024854,135.4959506&modes=transit&key=AIzaSyD66g27MXhvDFVCYdrAsu60XM91URf2UFY
        //transit 은 modes 라고 해야함.

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
        // 6. 경로를 찾을 수 없는 경우 최대 값을 반환
        return Integer.MAX_VALUE; // 경로를 찾을 수 없는 경우
    }

    public List<Employees> filterEmployeesForProject(Projects project) {
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
        List<Employees> finalEmployees = filterByCommutingTime(availableEmployees, project);
        long step3EndTime = System.currentTimeMillis();
        System.out.println("조건3 : " + finalEmployees);


        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " milliseconds");
        System.out.println("Step 1 (filterEmployeesByProjectRequirements) took: " + (step1EndTime - step1StartTime) + " milliseconds");
        System.out.println("Step 2 (filterByProjectDates) took: " + (step2EndTime - step2StartTime) + " milliseconds");
        System.out.println("Step 3 (filterByCommutingTime) took: " + (step3EndTime - step3StartTime) + " milliseconds");
        return finalEmployees;
    }


}
