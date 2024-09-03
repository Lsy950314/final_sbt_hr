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
            if (projectRequirementsService.checkFulfilledCount(projectId)) {
                projectsService.updateStatusTo(projectId, 1);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean matchCancel(Long projectId, Long employeeId, Long projectRequirementsId) {
        if (projectRequirementsService.decreaseFulfilledCount(projectRequirementsId) &&
                employeesProjectsService.deleteEmployeesProjects(employeeId, projectId) &&
                employeesService.restoreEndDates(employeeId) &&
                employeesService.updateAllocationTo(employeeId, -1)
        ) {
            projectsService.updateStatusTo(projectId, -1);
            return true;
        }
        return false;

    }

    @Value("${commuting.max-time-in-minutes}")
    private int maxCommutingTimeInMinutes;

    public Map<Employees, Integer> filterByCommutingTime(List<Employees> employees, Projects projects) {
        double projectLatitude = projects.getLatitude();
        double projectLongitude = projects.getLongitude();

        Map<String, Integer> transitTimeCache = new HashMap<>();

        List<CompletableFuture<AbstractMap.SimpleEntry<Employees, Integer>>> futures = employees.stream()
                .map(employee -> CompletableFuture.supplyAsync(() -> {
                    double employeeLatitude = employee.getLatitude();
                    double employeeLongitude = employee.getLongitude();
                    String cacheKey = employeeLatitude + "," + employeeLongitude + "->" + projectLatitude + "," + projectLongitude;

                    if (transitTimeCache.containsKey(cacheKey)) {
                        return new AbstractMap.SimpleEntry<>(employee, transitTimeCache.get(cacheKey));
                    }

                    try {
                        int transitTimeInMinutes = getTransitTimeInMinutes(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude);
                        transitTimeCache.put(cacheKey, transitTimeInMinutes);
                        return new AbstractMap.SimpleEntry<>(employee, transitTimeInMinutes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new AbstractMap.SimpleEntry<>(employee, -1);
                    }
                })).collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(entry -> entry.getValue() <= maxCommutingTimeInMinutes && entry.getValue() != -1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Google Directions API
    private int getTransitTimeInMinutes(double employeeLatitude, double employeeLongitude, double projectLatitude, double projectLongitude) throws IOException {
        int duration = getDurationWithMode(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude, "modes");

        if (duration == Integer.MAX_VALUE) {
            duration = getDurationWithMode(employeeLatitude, employeeLongitude, projectLatitude, projectLongitude, "mode");
        }

        return duration;
    }

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;
    private int getDurationWithMode(double employeeLatitude, double employeeLongitude, double projectLatitude, double projectLongitude, String modeKey) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(JSON_FACTORY.createJsonObjectParser()));

        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");
        url.put("origin", employeeLatitude + "," + employeeLongitude);
        url.put("destination", projectLatitude + "," + projectLongitude);
        url.put(modeKey, "transit");
        url.put("key", googleMapsApiKey);

        HttpResponse response = requestFactory.buildGetRequest(url).execute();
        Map<String, Object> directionsResponse = response.parseAs(Map.class);

        List<Map<String, Object>> routes = (List<Map<String, Object>>) directionsResponse.get("routes");

        if (routes != null && !routes.isEmpty()) {
            List<Map<String, Object>> legs = (List<Map<String, Object>>) routes.get(0).get("legs");

            if (legs != null && !legs.isEmpty()) {
                Map<String, Object> duration = (Map<String, Object>) legs.get(0).get("duration");

                if (duration != null) {
                    return ((Number) duration.get("value")).intValue() / 60;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    public Map<Employees, Integer> filterEmployeesForProject(Projects project) {

        List<Employees> filteredEmployeesByRequirements = findEmployeesByProjectRequirements(project);

        Map<Employees, Integer> finalEmployees = filterByCommutingTime(filteredEmployeesByRequirements, project);

        return finalEmployees;
    }
}
