package com.redis.repository;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.redis.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Integer> {
    Optional<Employee> findByName(String name);
}

