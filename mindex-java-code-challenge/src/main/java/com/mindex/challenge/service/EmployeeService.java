package com.mindex.challenge.service;

import com.mindex.challenge.data.Employee;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    ResponseEntity<Object> getEmployeesWithReportCounts(String employeeId);
}
