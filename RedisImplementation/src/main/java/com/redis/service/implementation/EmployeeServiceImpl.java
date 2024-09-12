package com.redis.service.implementation;

import com.redis.entity.Employee;
import com.redis.repository.EmployeeRepo;
import com.redis.service.EmployeeInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
//fetch all employees and save them in redis and whenever I need again that i should fetch them from redis instead of database. Data of employees will get change after 1 day. Again after 1 day I want to schedule one task to update new employees to redis.

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeInterface {

    private final EmployeeRepo employeeRepo;

    private final RedisTemplate<String, Object> redisTemplate;

    public Employee save(Employee e) {
        try {
            redisTemplate.opsForHash().put("employeesNameToSalary",e.getName(),String.valueOf(e.getSalary()));
            return employeeRepo.save(e);
        } catch (Exception e1) {
            throw new RuntimeException("Failed to save employee to database", e1);
        }
    }

    @Cacheable(value = "employees") //cache hit and cache miss and result will be stored in a cache named "employees".
    public List<Employee> findAll() {
        try {
            return employeeRepo.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch details of all the employees", e);
        }
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Run once a day
    @CacheEvict(value = "employees", allEntries = true)
    public void updateCache() {
        // Fetch updated employees and update the cache
        List<Employee> employees = employeeRepo.findAll();
        employees.forEach(employee -> redisTemplate.opsForHash().put("employees", employee.getId().toString(), employee));
    }

    public Optional<Employee> get(Integer id) {
        String cacheKey = id.toString();
        Employee cachedEmployee = (Employee) redisTemplate.opsForHash()
                .get("employees", cacheKey);
        if (cachedEmployee != null) {
            return Optional.of(cachedEmployee);
        }

        // If not found in cache, retrieve from database
        Optional<Employee> optionalEmployee = employeeRepo.findById(id);
        optionalEmployee.ifPresent(employee -> {
            // Cache the retrieved employee
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(cacheKey, employee);
        });
        return optionalEmployee;
    }

    public void delete(Integer id) {

        try {
            Optional<Employee> emp = employeeRepo.findById(id);
            if (emp.isPresent()) {
                Employee e = emp.get();
                employeeRepo.delete(e);
                redisTemplate.opsForHash().delete("employees", id.toString());
            }
        } catch (Exception e1) {
            throw new RuntimeException("Failed to delete employee with id: " + id, e1);
        }
    }

    @Override
    public String getEmployeeSalary(String name) {
        String salary =  (String) redisTemplate.opsForHash().get("employeesNameToSalary", name);
        if (salary == null) {
            Optional<Employee> optionalEmployee = employeeRepo.findByName(name);
            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                salary = String.valueOf(employee.getSalary());
                // Cache the salary in Redis
                redisTemplate.opsForHash().put("employeesNameToSalary", name, salary);
            } else {
                return "Employee not found";
            }
        }
        return salary;
    }
}


