package com.infineon.employee_service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/employee")
@RestController
@AllArgsConstructor
public class EmployeeController {

    private static final List<Employee> employees = new ArrayList<>();
    private static final String BASE_URL = "http://department-service/department/";
    private final WebClient.Builder webClientBuilder;

    static {
        employees.add(new Employee(1, "Ranjan", "Kumar", "ranjan.kumar@infineon.com", "123 Main St, Anytown, USA", 1));
        employees.add(new Employee(2, "Abubakar", "Ruknuddin", "abubakar.ruknuddin@infineon.com", "456 Elm St, Othertown, USA", 1));
        employees.add(new Employee(3, "Sakshi", "Gupta", "sakshi.gupta@infineon.com", "789 Oak St, Sometown, USA", 1));
        employees.add(new Employee(4, "Raju", "Bhadwal", "raju.bhadwal@infineon.com", "101 Pine St, Anycity, USA", 2));
        employees.add(new Employee(5, "David", "Wilson", "david.wilson@example.com", "202 Maple St, Othercity, USA", 2));
        employees.add(new Employee(6, "Sarah", "Brown", "sarah.brown@example.com", "303 Birch St, Somecity, USA", 3));
        employees.add(new Employee(7, "Chris", "Jones", "chris.jones@example.com", "404 Cedar St, Anothertown, USA", 4));
        employees.add(new Employee(8, "Amanda", "Garcia", "amanda.garcia@example.com", "505 Spruce St, Yetanothercity, USA", 5));
        employees.add(new Employee(9, "Daniel", "Martinez", "daniel.martinez@example.com", "606 Fir St, Thiscity, USA", 6));
        employees.add(new Employee(10, "Laura", "Hernandez", "laura.hernandez@example.com", "707 Redwood St, Thatcity, USA", 7));
        employees.add(new Employee(11, "John", "Doe", "john.doe@example.com", "123 Main St, Anytown, USA", 8));
        employees.add(new Employee(12, "Jane", "Smith", "jane.smith@example.com", "456 Elm St, Othertown, USA", 9));
        employees.add(new Employee(13, "Michael", "Johnson", "michael.johnson@example.com", "789 Oak St, Sometown, USA", 11));
    }

    // @RequestMapping("/allEmployees")
    // public ResponseEntity<List<Employee>> getDummyEmployeeInfo() {
    //     return new ResponseEntity<>(employees, HttpStatus.OK);
    // }

    @GetMapping("/allEmployees")
    public Mono<ResponseEntity<List<Employee>>> getDummyEmployeeInfo() {
        return Flux.fromIterable(employees)
                .flatMap(employee -> getDepartmentName(employee.getDepartmentId())
                        .map(departmentName -> new Employee(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getAddress(), employee.getDepartmentId(), departmentName)))
                .collectList()
                .map(ResponseEntity::ok);
    }

    private Mono<String> getDepartmentName(int departmentId) {
        String url = BASE_URL + departmentId;
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(responseBody -> (String) responseBody.get("name"))
                .onErrorReturn("");
    }
    
    @RequestMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        Optional<Employee> employee = employees.stream().filter(e -> e.getId() == id).findFirst();
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(new Employee(0, null, null, null, null, 0), HttpStatus.NOT_FOUND));
    }

    // @RequestMapping("/department/{deptId}")
    // public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable int deptId) {
    //     List<Employee> filteredEmployees = employees.stream().filter(e -> e.getDepartmentId() == deptId).toList();
    //     return new ResponseEntity<>(filteredEmployees, filteredEmployees.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    // }
    @RequestMapping("/department/{deptId}")
    public Mono<ResponseEntity<List<Employee>>> getEmployeesByDepartmentId(@PathVariable int deptId) {
        List<Employee> filteredEmployees = employees.stream().filter(e -> e.getDepartmentId() == deptId).toList();
        
        if (filteredEmployees.isEmpty()) {
            return Mono.just(new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND));
        }

        return Flux.fromIterable(filteredEmployees)
                .flatMap(employee -> getDepartmentName(employee.getDepartmentId())
                        .map(departmentName -> new Employee(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getAddress(), employee.getDepartmentId(), departmentName)))
                .collectList()
                .map(updatedEmployees -> new ResponseEntity<>(updatedEmployees, HttpStatus.OK));
    }
    
}