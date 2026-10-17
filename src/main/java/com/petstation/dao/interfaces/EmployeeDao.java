package com.petstation.dao.interfaces;

import com.petstation.model.Employee;

import java.sql.Connection;
import java.util.Optional;

public interface EmployeeDao {
    Optional<Employee> findById(Connection connection, long employeeId) throws Exception;
}
