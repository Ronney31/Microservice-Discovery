package com.infineon.department_service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Synchronized;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    private static final List<Department> DEPARTMENTS = Arrays.asList(
        new Department(1, "Information Technology"),
        new Department(2, "Human Resources"),
        new Department(3, "Finance"),
        new Department(4, "Marketing"),
        new Department(5, "Sales"),
        new Department(6, "Research and Development"),
        new Department(7, "Customer Service"),
        new Department(8, "Legal"),
        new Department(9, "Procurement"),
        new Department(10, "Facilities")
    );

    // private RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;
    private String employeeServiceUrl;

    public DepartmentController(
        // RestTemplate restTemplate, 
        WebClient.Builder webClientBuilder,
        @Value("${employee.service.url}") String employeeServiceUrl) {
        // this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
        this.employeeServiceUrl = employeeServiceUrl;
    }

    @RequestMapping("/allDepartments")
    public ResponseEntity<List<Department>> getDummyDepartmentInfo() {
        return new ResponseEntity<>(DEPARTMENTS, HttpStatus.OK);
    }

    @RequestMapping("/{deptId}")
    public ResponseEntity<Department> getDepartment(@PathVariable int deptId) {
        Department department = findDepartmentByParam(deptId);
        if (department != null) {
            return ResponseEntity.ok(department);
        } else {
            return new ResponseEntity<>(new Department(0, null), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{deptId}/employees")
    public Mono<ResponseEntity<Map<String, Object>>> getDepartmentWithEmployees(@PathVariable int deptId) {
        Department department = findDepartmentByParam(deptId);
        if (department == null) {
            return Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        String url = employeeServiceUrl + "/department/" + deptId;

        return webClientBuilder.build().get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(employees -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("department", department);
                    result.put("employees", employees);
                    return ResponseEntity.ok(result);
                });
    }

    private Department findDepartmentByParam(int deptId) {
        for (Department department : DEPARTMENTS) {
            if (department.id() == deptId) {
                return department;
            }
        }
        return null;
    }
    
}
