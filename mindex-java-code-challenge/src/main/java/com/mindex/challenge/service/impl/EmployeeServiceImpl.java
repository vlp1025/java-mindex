package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.APIError;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.helpers.Constants;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ResponseEntity<Object> getEmployeesWithReportCounts(String employeeId) {
        Employee employee = null;
        int count =0;
        try {
            employee = employeeRepository.findByEmployeeId(employeeId);
            List<Integer> maxCount = new ArrayList<>();
            count = iterateThroughEmployees(employee,0, maxCount);
        }catch (Exception exception) {
            return new ResponseEntity<>(APIError
                    .builder()
                    .message(exception.getMessage())
                    .reason(exception.getCause() != null?
                            exception.getCause().getMessage() :
                            Constants.ERROR_HAS_OCCURRED)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (employee != null) {
            if (employee.getDirectReports()!= null) {

                List<Employee> employees = employee.getDirectReports().stream()
                        .map(member -> employeeRepository
                                .findByEmployeeId(member.getEmployeeId())).collect(Collectors.toList());
                employee.setDirectReports(employees);
            }
            return new ResponseEntity<>(ReportingStructure
                    .builder()
                    .employee(employee)
                    .numberOfReports(employee.getDirectReports() != null ?
                            count: 0)
                    .build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(APIError
                .builder()
                .message(Constants.ID_WAS_NOT_FOUND.replace("{}", employeeId))
                .reason(Constants.DOES_NOT_EXIST)
                .build(), HttpStatus.NOT_FOUND);
    }

    private int iterateThroughEmployees(Employee employee, int count, List<Integer> maxCount) {
        int finalCount = 0;
        if (employee.getDirectReports() != null && !employee.getDirectReports().isEmpty()){
            finalCount = count + (int) employee.getDirectReports()
                    .stream().count();
            maxCount.add(finalCount);
            int finalCount1 = finalCount;
            employee.getDirectReports().stream()
                    .forEach(member -> {
                        Employee employeeMember = employeeRepository
                                .findByEmployeeId(member.getEmployeeId());
                        if (employeeMember.getDirectReports()!= null && !employeeMember.getDirectReports().isEmpty())
                            iterateThroughEmployees(employeeMember, finalCount1, maxCount);
                        return;
                    });
            return Collections.max(maxCount);
        }else {
            return count;
        }

    }
}
