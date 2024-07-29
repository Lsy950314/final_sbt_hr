package com.example.sbt_final_hr.domain.service;

import com.example.sbt_final_hr.domain.model.entity.Employees;
import com.example.sbt_final_hr.domain.model.entity.EmployeesSkill;
import com.example.sbt_final_hr.domain.model.entity.ProjectRequirements;
import com.example.sbt_final_hr.domain.model.entity.Projects;
import com.example.sbt_final_hr.domain.repository.EmployeesRepository;
import com.example.sbt_final_hr.domain.repository.ProjectRequirementsRepository;
import com.example.sbt_final_hr.domain.repository.ProjectsRepository;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final EmployeesRepository employeesRepository;
    private final ProjectsRepository projectsRepository;
    private final ProjectRequirementsRepository projectRequirementsRepository;

    public MatchService(EmployeesRepository employeesRepository, ProjectsRepository projectsRepository, ProjectRequirementsRepository projectRequirementsRepository) {
        this.employeesRepository = employeesRepository;
        this.projectsRepository = projectsRepository;
        this.projectRequirementsRepository = projectRequirementsRepository;
    }
    //3번조건 때문에 추가중


    //private static final JsonFactory  JSON_FACTORY = GsonFactory.getDefaultInstance();


    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();



    // 첫 번째 조건: 프로젝트의 요구조건(스킬스택)에 부합하는 사원 필터링
    public List<Employees> filterEmployeesByProjectRequirements(Projects project) {
        List<Employees> allEmployees = employeesRepository.findAll();

        List<ProjectRequirements> projectRequirements = projectRequirementsRepository.findByProject_ProjectId(project.getProjectId());
//        System.out.println(projectRequirements);
        return allEmployees.stream()
                .filter(employee -> hasRequiredSkills(employee, projectRequirements))
                .collect(Collectors.toList());
    }

    // 사원이 프로젝트 요구사항을 충족하는지 확인하는 메서드
    private boolean hasRequiredSkills(Employees employee, List<ProjectRequirements> projectRequirements) {
        List<EmployeesSkill> employeeSkills = employee.getSkills();
//        System.out.println("Employee: " + employee.getName());
//        System.out.println("Employee Skills: " + employeeSkills);

        for (ProjectRequirements requirement : projectRequirements) {
//            System.out.println("Requirement: " + requirement.getSkill().getSkillName() + ", Required Experience: " + requirement.getRequiredExperience());

            boolean matches = employeeSkills.stream().anyMatch(skill -> {
                boolean skillNameMatches = skill.getSkill().getSkillName().equals(requirement.getSkill().getSkillName());
                boolean experienceMatches = skill.getSkillCareer() >= requirement.getRequiredExperience();

//                System.out.println("Checking skill: " + skill.getSkill().getSkillName() + ", Career: " + skill.getSkillCareer());
//                System.out.println("Skill Name Matches: " + skillNameMatches + ", Experience Matches: " + experienceMatches);

                return skillNameMatches && experienceMatches;
            });

            if (matches) {
//                System.out.println("Requirement met: " + requirement.getSkill().getSkillName());
                return true; // 하나라도 부합하면 true 반환
            }
        }

        return false; // 모든 요구조건에 부합하지 않으면 false 반환
    }


    public List<Employees> filterByProjectDates(List<Employees> employees, LocalDate projectStartDate) {
        return employees.stream()
                .filter(employee -> employee.getCurrentProjectEndDate() != null && employee.getCurrentProjectEndDate().isBefore(projectStartDate))
                .collect(Collectors.toList());
    }




    //3번째 조건 : 통근시간 기준으로 자르기 일단 90분
    public List<Employees> filterByCommutingTime(Projects projects) {
        //1.프로젝트 위치 정보 추출
        double projectLatitude = projects.getLatitude();
        double projectLongitude = projects.getLongitude();

        //2.모든 직원 데이터 조회
        List<Employees> allEmployees = employeesRepository.findAll();

        //최대 통근시간 기준 90분, 100분, 110분
        int maxCommutingTimeInMinutes  = 90;

        return allEmployees.stream()
                .filter(employee -> {
                    double employeeLatitude = employee.getLatitude();
                    double employeeLongitude = employee.getLongitude();
                    try {
                        int transitTimeInMinutes = getTransitTimeInMinutes(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude);
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
        //1. HTTP 요청을 생성하는 팩토리 객체 생성
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(JSON_FACTORY.createJsonObjectParser()));
        //??Map<String, Object> directionsResponse = response.parseAs(Map.class, JSON_FACTORY); // GsonFactory를 사용하여 JSON 응답을 파싱
        // 2. Google Directions API의 요청 URL 설정
        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");
        url.put("origin", employeeLatitude + "," + employeeLongitude);
        url.put("destination", projectLatitude + "," + projectLongitude);
        url.put("mode", "transit");
        url.put("key", "AIzaSyD66g27MXhvDFVCYdrAsu60XM91URf2UFY");
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
        // 6. 경로를 찾을 수 없는 경우 최대 값을 반환
        return Integer.MAX_VALUE; // 경로를 찾을 수 없는 경우
    }

    //3번째 조건 : 통근시간 기준으로 자르기 일단 90분
//    public List<Employees> filterByCommutingTime(Projects project) {
//        List<Employees> allEmployees = employeesRepository.findAll();
//        //return allEmployees;
//        //직원의 위도, 경도 와 안건의 위도, 경도 값 받아서 directions API 로 통근시간 가져오기. 90분 이하인 경우의 Employees만 가져오기 .
//
//
//        //return employees.stream() <= 결과적으로는 이런식으로다가 나오게.
//              //  .filter(employee -> employee.getCurrentProjectEndDate() != null && employee.getCurrentProjectEndDate().isBefore(projectStartDate))
//             //   .collect(Collectors.toList());
//
//    }
//    //특정(?)사원이 통근거리 요구사항을 충족하는지 확인하는 메서드 : 일단 90분
//    private boolean isShorterThanStandardCommutingTime(Employees employees, Projects projects) {
//        //List<Employees> allEmployees = employeesRepository.findAll(); //사원 다 가져오는거 맞지 ?
//        //특정 직원과 특정 프로젝트의 위도, 경도 get
//        double employeesLatitude = employees.getLatitude();
//        double employeesLongitude = employees.getLongitude();
//        double projectsLatitude = projects.getLatitude();
//        double projectsLongitude = projects.getLongitude();
//
//        //최대 통근시간 기준 90분, 100분, 110분
//        int transitTime90Minutes = 90;
//        int transitTime100Minutes = 100;
//        int transitTime110Minutes = 110;
//
//        try {
//            int transitTimeInMinutes = getTransitTimeInMinutes(employeesLatitude, employeesLongitude, projectsLatitude, projectsLongitude);
//            if (transitTimeInMinutes <= transitTime90Minutes) {
//                return true;
//            }
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//    //????
//    //getAllEmployeesWithCoordinates 메서드는 EmployeesRepository를 사용하여 데이터베이스에서 모든 직원(Employees) 엔티티를 조회하고, 각 직원의 위도(latitude)와 경도(longitude) 정보를 출력한 다음, 조회한 직원 목록을 반환하는 메서드입니다. 이 메서드는 직원 데이터의 위도와 경도 정보를 확인하고자 할 때 유용합니다.
//    //필요한지 모르겠다.
//    public List<Employees> getAllEmployeesWithCoordinates() {
//        List<Employees> allEmployees = employeesRepository.findAll();
//
//        allEmployees.forEach(employee -> {
//            double employeeLat = employee.getLatitude();
//            double employeeLng = employee.getLongitude();
//            System.out.println("Employee ID: " + employee.getEmployeeId() + ", Latitude: " + employeeLat + ", Longitude: " + employeeLng);
//        });
//
//        return allEmployees;
//    }










}
