package com.infineon.employee_service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;

@RequestMapping("/employee")
@RestController
@AllArgsConstructor
public class EmployeeController {

    private static final List<Employee> employees = new ArrayList<>();
    private static final String BASE_URL = "http://localhost:8093/department/";
    private RestTemplate restTemplate;

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

    @RequestMapping("/allEmployees")
    public ResponseEntity<List<Employee>> getDummyEmployeeInfo() {
        for (Employee employee : employees) {
            String departmentName = getDepartmentName(employee.getDepartmentId());
            employee.setDepartmentName(departmentName);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    private String getDepartmentName(int departmentId) {
        String url = BASE_URL + departmentId;
        Map<String, Object> responseBody = null;
        try {
            var response = restTemplate.getForEntity(url, Map.class);
            responseBody = response.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return responseBody != null ? (String) responseBody.get("name") : "";
    }
    
    @RequestMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) {
        Optional<Employee> employee = employees.stream().filter(e -> e.getId() == id).findFirst();
        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(new Employee(0, null, null, null, null, 0), HttpStatus.NOT_FOUND));
    }

    @RequestMapping("/department/{deptId}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable int deptId) {
        List<Employee> filteredEmployees = employees.stream().filter(e -> e.getDepartmentId() == deptId).toList();
        return new ResponseEntity<>(filteredEmployees, filteredEmployees.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
    
}