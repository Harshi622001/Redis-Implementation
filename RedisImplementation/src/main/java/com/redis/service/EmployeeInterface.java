package com.redis.service;

import com.redis.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeInterface {
    Employee save(Employee e);

    List<Employee> findAll();

    Optional<Employee> get(Integer id);

    void delete(Integer id);

    String getEmployeeSalary(String name);
}