package com.redis.controller;

import com.redis.entity.Employee;
import com.redis.service.EmployeeInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeInterface employeeInterface;

    @PostMapping("/save")
    public Employee save(@RequestBody Employee e) {
        return employeeInterface.save(e);
    }

    @GetMapping("/find")
    public List<Employee> findall() {
        return employeeInterface.findAll();
    }

    @PostMapping("/get/{id}")
    public Optional<Employee> get(@PathVariable("id") Integer id) {
        return employeeInterface.get(id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        employeeInterface.delete(id);
    }


    @GetMapping("/salary")
    public String getEmployeeSalary(@RequestBody String name){
        return employeeInterface.getEmployeeSalary(name);
    }


}