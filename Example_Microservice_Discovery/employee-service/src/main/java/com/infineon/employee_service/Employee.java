package com.infineon.employee_service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Employee {
    public Employee(int id, String firstName, String lastName, String email, String address, int departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.departmentId = departmentId;
    }
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private int departmentId;
    private String departmentName;
}